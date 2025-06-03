from datetime import timedelta
from django.utils import timezone
from django.contrib.auth.models import AbstractUser
from django.db import models
from django.core.validators import RegexValidator
from rest_framework_simplejwt.tokens import RefreshToken
from .utils import send_OTP


class User(AbstractUser):
    phone_number = models.CharField(
        max_length=11,
        validators=[
            RegexValidator(
                regex=r'^\+?\d{9,}$',
                message="شماره تلفن باید به شکل 09123456789 وارد شود"
            ),
        ],
        unique=True
    )
    
    image = models.ImageField(upload_to="profile_images", blank=True, null=True)
    is_verified = models.BooleanField(default=False)
    verification_code = models.CharField(max_length=8,null=True,blank=True,unique=True)
    expire_at = models.DateTimeField(null=True,blank=True)
    is_banned = models.BooleanField(default=False)
    
    def send_code(self):
        self.verification_code = send_OTP(self.phone_number,'Polaris Verification Code:')
        self.expire_at = timezone.now() + timedelta(minutes=10)
        self.is_verified = False
        self.save()
        return True
    
    def verify(self,code):
        if code == self.verification_code and timezone.now()<self.expire_at:
            self.is_verified = True
            self.verification_code = None
            self.save()
            return True
        else:
            return False
    
    def tokens(user):
        refresh = RefreshToken.for_user(user)
        return {
            "access":str(refresh.access_token),
            "refresh":str(refresh)
        }
    
    def __str__(self):
        return self.username