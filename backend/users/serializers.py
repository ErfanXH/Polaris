from rest_framework import serializers
from rest_framework import status
from rest_framework.exceptions import PermissionDenied, NotFound
from django.contrib.auth.password_validation import validate_password
from django.db.models import Q
from django.core.validators import RegexValidator
from re import match
from .models import *

class RegisterSerializer(serializers.Serializer):
    phone_number = serializers.CharField(write_only=True,validators = [RegexValidator(regex=r'^09\d{9,10}$')])
    email = serializers.EmailField(write_only=True)
    password = serializers.CharField(write_only=True,validators=[validate_password])
    
    def validate(self, attrs):
        user = User.objects.filter(Q(phone_number=attrs['phone_number']) | Q(email=attrs['email']))
        if len(user.values()) >=2 :
            raise serializers.ErrorDetail("there are users with this phone number and email",status.HTTP_409_CONFLICT)
        elif len(user.values()) == 1 and user.first().is_verified:
            raise serializers.ErrorDetail("there is a user with this phone number and emsil",status.HTTP_409_CONFLICT)
        elif len(user.values()) == 1 and not user.first().is_verified:
            if not user.first().check_password(attrs['password']):
                PermissionDenied('Password is incorrect')
            else:
                attrs['exist']=True
                attrs['user']= user.first()
        else:
            attrs['exist']=True
        return super().validate(attrs)


class LoginSerializer(serializers.ModelSerializer):
    number_or_email = serializers.CharField()
    password = serializers.CharField(validators=[validate_password])
    class Meta:
        model = User
        fields = ['number_or_email','password','phone_number','username','email','image','is_staff']
        read_only_fields = ['phone_number','username','email','image','is_staff']
        write_only_fields = ['number_or_email','password']
        
    def validate(self, attrs):
        if match(r'^09\d{9,10}$', attrs['number_or_email']):
            attrs['phone_number'] = attrs['number_or_email']
            user = User.objects.filter(phone_number=attrs['phone_number']).first()
        elif match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', attrs['number_or_email']):
            attrs['email'] = attrs['number_or_email']
            user = User.objects.filter(email=attrs['email']).first()
        else:
            raise serializers.ValidationError('number_or_email field must be a phone number or email')
        attrs.pop('number_or_email')
        
        if not user:
            NotFound('no user found')
        elif user and not user.check_password(attrs['password']):
            PermissionDenied('Password is incorrect')
        else:
            attrs['exists']=True
            attrs['verified']=user.is_verified
            attrs['user']= user
        return super().validate(attrs)
    
    def to_representation(self, instance):
        return {
            *super().to_representation(self, instance),
            *instance.token()
        }
        
        
class VerifyCodeSerializer(serializers.Serializer):
    number_or_email = serializers.CharField(write_only = True)
    code = serializers.CharField(required = False)
    
    def validate(self, attrs):
        if match(r'^09\d{9,10}$', attrs['number_or_email']):
            attrs['phone_number'] = attrs['number_or_email']
            user = User.objects.filter(phone_number=attrs['phone_number']).first()
        elif match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', attrs['number_or_email']):
            attrs['email'] = attrs['number_or_email']
            user = User.objects.filter(email=attrs['email']).first()
        else:
            raise serializers.ValidationError('number_or_email field must be a phone number or email')
        attrs.pop('number_or_email')
        
        if not user:
            NotFound('no user found')
        else:
            attrs['user'] = user
        return super().validate(attrs)


class VerificationSerializer(serializers.ModelSerializer):
    number_or_email = serializers.CharField()
    password = serializers.CharField(validators=[validate_password])
    code = serializers.CharField()
    class Meta:
        model = User
        fields = ['number_or_email','password','phone_number','username','email','image','code','is_staff']
        read_only_fields = ['phone_number','username','email','image','is_staff']
        write_only_fields = ['number_or_email','password','code']
        
    def validate(self, attrs):
        if match(r'^09\d{9,10}$', attrs['number_or_email']):
            attrs['phone_number'] = attrs['number_or_email']
            user = User.objects.filter(phone_number=attrs['phone_number']).first()
        elif match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', attrs['number_or_email']):
            attrs['email'] = attrs['number_or_email']
            user = User.objects.filter(email=attrs['email']).first()
        else:
            raise serializers.ValidationError('number_or_email field must be a phone number or email')
        attrs.pop('number_or_email')
        
        if not user:
            NotFound('no user found')
        elif user and not user.check_password(attrs['password']):
            PermissionDenied('Password is incorrect')
        else:
            if not user.verify(attrs['code']):
                raise serializers.ErrorDetail('verification code does not match' , status.HTTP_406_NOT_ACCEPTABLE)
        return super().validate(attrs)

    def to_representation(self, instance):
        return {
            *super().to_representation(self, instance),
            *instance.token()
        }
        
        
class ProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = '__all__'