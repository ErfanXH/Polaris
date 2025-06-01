import random
from datetime import timedelta
from django.utils import timezone
from django.contrib.auth.models import AbstractUser
from django.db import models
from django.core.validators import RegexValidator
from rest_framework_simplejwt.tokens import RefreshToken
from django.core.mail import send_mail
from django.conf import settings

SENDER_EMAIL = settings.EMAIL_HOST_USER


class User(AbstractUser):

    phone_number = models.CharField(
        max_length=11,
        validators=[
            RegexValidator(
                regex=r'^\+?\d{9}$',
                message="شماره تلفن باید به شکل 09123456789 وارد شود"
            ),
        ],
        unique=True
    )
    is_verified = models.BooleanField(default=False)
    image = models.ImageField(upload_to="profile_images", blank=True, null=True)
    image = models
    def __str__(self):
        return self.username

    
    
    
class EmailVerification(models.Model):
    user = models.OneToOneField(User,on_delete=models.CASCADE,related_name='email_verification')
    code = models.CharField(max_length=5,null=True,blank=True)
    expire_at = models.DateTimeField(null=True,blank=True)

    
    def __str__(self):
        return self.user.username
    
    def is_expired(self):
        return self.expire_at < timezone.now()
    
    def is_valid(self,code):
        return self.code == code and not self.is_expired()
    
    def generate_code(self):
        return ''.join([str(random.randint(0,9)) for _ in range(5)])
    

    
    def send_verification_email(self):
        self.code = self.generate_code()
        self.expire_at = timezone.now() + timedelta(minutes=10)
        self.save()
        send_mail(
            'کد تایید ایمیل',
            f'کد تایید شما: {self.code}',
            SENDER_EMAIL,
            [self.user.email],
            fail_silently=False)
        

class ForgetPasswordVerification(models.Model):
    user = models.OneToOneField(User,on_delete=models.CASCADE,related_name='forget_password_verification')
    code = models.CharField(max_length=5,null=True,blank=True)
    expire_at = models.DateTimeField(null=True,blank=True)

    
    def __str__(self):
        return self.user.username
    
    def is_expired(self):
        return self.expire_at < timezone.now()
    
    def is_valid(self,code):
        return self.code == code and not self.is_expired()
    
    def generate_code(self):
        return ''.join([str(random.randint(0,9)) for _ in range(5)])
    

    
    def send_verification_email(self):
        self.code = self.generate_code()
        self.expire_at = timezone.now() + timedelta(minutes=10)
        self.save()
        send_mail(
            'کد بازیابی رمز عبور ایران سند',
            f'کد بازیابی رمز عبور شما: {self.code}',
            SENDER_EMAIL,
            [self.user.email],
            fail_silently=False)
            
class SignupEmailVerification(models.Model):
    email = models.EmailField(unique=True)
    code = models.CharField(max_length=5,null=True,blank=True)
    expire_at = models.DateTimeField(null=True,blank=True)
    is_verified = models.BooleanField(default=False,blank=True)
    
    
    def __str__(self):
        return self.user.username
    
    def is_expired(self):
        return self.expire_at < timezone.now()
    
    def is_valid(self,code):
        return self.code == code and not self.is_expired()
    
    def generate_code(self):
        return ''.join([str(random.randint(0,9)) for _ in range(5)])
    

    
    def send_verification_email(self):
        self.code = self.generate_code()
        self.expire_at = timezone.now() + timedelta(minutes=10)
        self.save()
        send_mail(
            'کد تایید ایمیل ایران سند',
            f'کد تایید شما:{self.code}',
            SENDER_EMAIL,
            [self.email],
            fail_silently=False)