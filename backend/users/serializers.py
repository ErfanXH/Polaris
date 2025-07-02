from rest_framework import serializers
from rest_framework import status
from rest_framework.exceptions import PermissionDenied, NotFound ,APIException
from django.contrib.auth.password_validation import validate_password
from django.db.models import Q
from re import match
from django.conf import settings
from .models import *

class RegisterSerializer(serializers.Serializer):
    phone_number = serializers.CharField(write_only=True)
    email = serializers.EmailField(write_only=True)
    password = serializers.CharField(write_only=True,validators=[validate_password])
    
    def validate(self, attrs):
        if not match(r'^09\d{9}$',attrs['phone_number']):
            raise serializers.ValidationError("phone number must be like 09xxxxxxxxx and 11 digit in total")
        user = User.objects.filter(Q(phone_number=attrs['phone_number']) | Q(email=attrs['email']))
        if len(user.values()) >=2 :
            raise serializers.ValidationError("there are users with this phone number and email",status.HTTP_409_CONFLICT)
        elif len(user.values()) == 1 and user.first().is_verified:
            raise serializers.ValidationError("there is a user with this phone number and email",status.HTTP_409_CONFLICT)
        elif len(user.values()) == 1 and not user.first().is_verified:
            if user.first().phone_number != attrs['phone_number'] or user.first().email !=attrs['email']:
                raise serializers.ValidationError("phone number and email does not match", status.HTTP_409_CONFLICT)
            elif not user.first().check_password(attrs['password']):
                raise PermissionDenied('Password is incorrect')
            else:
                attrs['exist']=True
                attrs['user']= user.first()
        else:
            attrs['exist']=False
        return super().validate(attrs)


class LoginSerializer(serializers.Serializer):
    number_or_email = serializers.CharField(write_only = True)
    password = serializers.CharField(validators=[validate_password], write_only = True)
        
    def validate(self, attrs):
        if match(r'^09\d{9}$', attrs['number_or_email']):
            user = User.objects.filter(phone_number=attrs['number_or_email']).first()
        elif match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', attrs['number_or_email']):
            user = User.objects.filter(email=attrs['number_or_email']).first()
        else:
            raise serializers.ValidationError('number_or_email field must be a phone number or email')
        attrs.pop('number_or_email')
        if not user:
            raise NotFound('no user found')
        elif user and not user.check_password(attrs['password']):
            raise PermissionDenied('Password is incorrect')
        else:
            attrs['user']= user
        return attrs
    
    def to_representation(self, instance):
        representation = {
            'phone_number': instance.phone_number,
            'email': instance.email,
            'username': instance.username,
            'image': instance.image.url if instance.image else None,
            'is_admin': instance.is_staff,
            'is_banned': instance.is_banned,
        }
        representation.update(instance.token())
        return representation
        
        
        
class VerifyCodeSerializer(serializers.Serializer):
    number_or_email = serializers.CharField(write_only = True)
    code = serializers.CharField(allow_blank=False)
    
    def validate(self, attrs):
        if match(r'^09\d{9}$', attrs['number_or_email']):
            user = User.objects.filter(phone_number=attrs['number_or_email']).first()
        elif match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', attrs['number_or_email']):
            user = User.objects.filter(email=attrs['number_or_email']).first()
        else:
            raise serializers.ValidationError('number_or_email field must be a phone number or email')
        attrs.pop('number_or_email')
        
        if not user:
            raise NotFound('no user found')
        else:
            attrs['user'] = user
        return super().validate(attrs)

class GetVerificationCodeSerializer(serializers.Serializer):
    number_or_email = serializers.CharField(write_only = True)
    
    def validate(self, attrs):
        if match(r'^09\d{9}$', attrs['number_or_email']):
            user = User.objects.filter(phone_number=attrs['number_or_email']).first()
        elif match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', attrs['number_or_email']):
            user = User.objects.filter(email=attrs['number_or_email']).first()
        else:
            raise serializers.ValidationError('number_or_email field must be a phone number or email')
        attrs.pop('number_or_email')
        
        if not user:
            raise NotFound('no user found')
        else:
            attrs['user'] = user
        return super().validate(attrs)

class VerificationSerializer(serializers.Serializer):
    number_or_email = serializers.CharField(write_only = True)
    password = serializers.CharField(validators=[validate_password],write_only = True)
    code = serializers.CharField(write_only = True)
        
    def validate(self, attrs):
        if match(r'^09\d{9}$', attrs['number_or_email']):
            user = User.objects.filter(phone_number=attrs['number_or_email']).first()
        elif match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', attrs['number_or_email']):
            user = User.objects.filter(email=attrs['number_or_email']).first()
        else:
            raise serializers.ValidationError('number_or_email field must be a phone number or email')
        attrs.pop('number_or_email')
        
        if not user:
            raise NotFound('no user found')
        else:
            attrs['result'] = user.verify_user(attrs['code'])
            attrs['user'] = user
        return attrs

    def to_representation(self, instance):
        representation = {
            'phone_number': instance.phone_number,
            'email': instance.email,
            'username': instance.username,
            'image': instance.image.url if instance.image else None,
            'is_admin': instance.is_staff,
            'is_banned': instance.is_banned,
        }
        representation.update(instance.token())
        return representation
            
        
class ProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ["phone_number","email","username","image","is_staff","is_banned","date_joined","allow_admin_access"]
        read_only_fields = ["phone_number","email","is_staff","is_banned","date_joined"]

class ChangePasswordSerializer(serializers.Serializer):
    password = serializers.CharField(validators=[validate_password],write_only = True)
    new_password = serializers.CharField(validators=[validate_password],write_only = True)
    
    def validate(self, attrs):
        if not self.context['user'].check_password(attrs['password']):
            raise serializers.ValidationError('given password do not match with you current password')
        else:
            return attrs



class AdminPasswordSerializer(serializers.Serializer):
    admin_password = serializers.CharField(write_only = True)
    
    def validate(self, attrs):
        if hash(attrs['admin_password']) != settings.ADMIN_PASSWORD:
            raise PermissionDenied('admin password is incorrect')
        return attrs
    
    

class SelectUserSerializer(serializers.Serializer):
    number_or_email = serializers.CharField(write_only = True)
    
    
    def validate(self, attrs):
        if match(r'^09\d{9}$', attrs['number_or_email']):
            user = User.objects.filter(phone_number=attrs['number_or_email']).first()
        elif match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', attrs['number_or_email']):
            user = User.objects.filter(email=attrs['number_or_email']).first()
        else:
            raise serializers.ValidationError('number_or_email field must be a phone number or email')
        attrs.pop('number_or_email')
        
        if not user:
            raise NotFound('no user found')
        else:
            attrs['user'] = user
        return super().validate(attrs)
    
    def to_representation(self, instance):
        representation = {
            'id': instance.id,
            'phone_number': instance.phone_number,
            'email': instance.email,
            'username': instance.username,
            'image': instance.image.url if instance.image else None,
            'is_admin': instance.is_staff,
            'is_superuser':instance.is_superuser,
            'is_banned': instance.is_banned,
            'is_verified': instance.is_verified,
            'allow_admin_access': instance.allow_admin_access
        }
        return representation