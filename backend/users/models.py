from datetime import timedelta
from django.utils import timezone
from django.contrib.auth.models import AbstractUser
from django.db import models
from django.core.validators import RegexValidator
from rest_framework_simplejwt.tokens import RefreshToken
from django.conf import settings
from random import randint
from .utils import send_OTP, name_generator,file_name
from uuid import uuid4
SENDER_EMAIL = settings.EMAIL_HOST_USER

def name_generator():
    return uuid4().hex[:30]

class User(AbstractUser):
    phone_validator=RegexValidator(
                regex=r'^09\d{9}$',
                message="Phone number must be like 09123456789")
    phone_number = models.CharField(max_length=11,validators=[phone_validator],unique=True)
    username = models.CharField(max_length=150,null=True,blank=True,default=name_generator,unique=False)
    email = models.EmailField(unique=True)
    image = models.ImageField(upload_to=file_name, blank=True, null=True)
    is_verified = models.BooleanField(default=False)
    verification_code = models.CharField(max_length=8,null=True,blank=True,unique=True)
    expire_at = models.DateTimeField(null=True,blank=True) #if is_verified==true,this field show last validated date
    is_banned = models.BooleanField(default=False)
    allow_admin_access = models.BooleanField(default=True)
    USERNAME_FIELD = 'phone_number'
    REQUIRED_FIELDS = ['email',]
    def send_code(self):
        if self.verification_code!=None and self.expire_at > timezone.now() + timedelta(minutes=7) :
            pass #logic for repeated request for getting code
        if settings.VERIFICATION_METHOD =='phone_number':
            self.verification_code = send_OTP(self.phone_number,'Polaris Verification Code:')
            
        elif settings.VERIFICATION_METHOD =='email':
            self.verification_code = ''.join([str(randint(0,9)) for _ in range(5)])
            self.email_user('polaris validation code', 
                            f'Here is your validation code\n Code:{self.verification_code}',
                            SENDER_EMAIL)
            
        self.expire_at = timezone.now() + timedelta(minutes=15)
        self.is_verified = False
        self.save()
        return True
    
    def verify_code(self,code):
        return code == self.verification_code
    
    def is_code_expired(self):
        return timezone.now()>self.expire_at
    
    def make_admin(self):
        self.is_staff = True
        self.save()
        return True
    
    def make_superuser(self):
        self.is_staff = True
        self.is_superuser = True
        self.save()
        return True
    
    def ban(self):
        self.is_banned = True
        self.save()
    
    def unban(self):
        self.is_banned = False
        self.save()
    
    def verify_user(self,code):
        if self.verify_code(code) and not self.is_code_expired():
            self.is_verified = True
            self.verification_code = None
            self.expire_at = timezone.now()
            self.save()
            return "verified"
        elif self.is_code_expired():
            return 'expired'
        elif not self.verify_code(code):
            return 'mismatch'
        else:
            return 'unknown'
    
    def token(self):
        refresh = RefreshToken.for_user(self)
        return {
            "access":f"JWT {str(refresh.access_token)}",
            "refresh":f"JWT {str(refresh)}"
        }      
    def __str__(self):
        return self.username