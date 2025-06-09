from datetime import timedelta
from django.utils import timezone
from django.contrib.auth.models import AbstractUser
from django.db import models
from django.core.validators import RegexValidator
from rest_framework_simplejwt.tokens import RefreshToken
from django.conf import settings
from random import randint
from .utils import send_OTP, name_generator
from uuid import uuid4
import os
SENDER_EMAIL = settings.EMAIL_HOST_USER


def file_name(instance, filename):
    ext = filename.split('.')[-1]
    filename = f'{uuid4().hex}.{ext}'
    return os.path.join("profile_images/", filename)

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
    USERNAME_FIELD = 'phone_number'
    REQUIRED_FIELDS = []
    
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