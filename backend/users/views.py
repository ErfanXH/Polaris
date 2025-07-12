from django.contrib.auth.models import User
from .serializers import *
from .models import *
from rest_framework import status
from rest_framework.response import Response
from rest_framework.permissions import AllowAny, IsAuthenticated, IsAdminUser
from rest_framework.views import APIView
from rest_framework.viewsets import GenericViewSet
from rest_framework.parsers import MultiPartParser, FormParser
from drf_yasg.utils import swagger_auto_schema
from rest_framework.decorators import action
from uuid import uuid4

class AuthenticationViewSet(GenericViewSet):
    def get_serializer_class(self):
        if self.action == 'register':
            return RegisterSerializer
        elif self.action == 'login':
            return LoginSerializer
        elif self.action=="verify_code":
            return VerifyCodeSerializer
        elif self.action=="get_verification_code":
            return GetVerificationCodeSerializer
        elif self.action=="verification":
            return VerificationSerializer
    permission_classes = [AllowAny]
    
    @action(detail=False, methods=['POST'])
    def register(self,request):
        auth_status = {}
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        validated_data=serializer.validated_data
        
        if not validated_data['exist']:
            auth_status['register_state'] = 'created'
            user = User.objects.create_user(
                username=uuid4(),
                phone_number=validated_data['phone_number'],
                email=validated_data['email'],
                password=validated_data['password'])
        else:
            auth_status['register_state'] = 'existed'
            user = validated_data['user']
            if user.is_banned:
                return Response({'detail':'this account is banned and cannot be accessed'}, status=status.HTTP_403_FORBIDDEN)
        try:
            user.send_code()
        except:
            return Response('could not send OTP code,please try again' , status=status.HTTP_502_BAD_GATEWAY)
        
        auth_status['detail'] = 'OTP code has been sent'
        return Response(auth_status, status=status.HTTP_201_CREATED)
    
    @action(detail=False, methods=['POST'])
    def login(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        validated_data=serializer.validated_data
        user = validated_data['user']
        if user.is_banned:
            return Response({'detail':'this account is banned and cannot be accessed'}, status=status.HTTP_403_FORBIDDEN)
        elif user.is_verified:
            user.last_login = timezone.now()
            user.save()
            return Response(serializer.to_representation(user), status=status.HTTP_202_ACCEPTED)
        else:
            return Response({'detail' : 'verification required, verify with code sent to your email'}, status=status.HTTP_401_UNAUTHORIZED)
        
    @action(detail=False, methods=['POST'])
    def verify_code(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user=serializer.validated_data['user']
        if user.is_code_expired():
            return Response({'detail':'verification code has expired'} , status=status.HTTP_406_NOT_ACCEPTABLE)
        elif not user.verify_code(serializer.validated_data['code']):
            return Response({'detail':'verification code does not match'} , status=status.HTTP_406_NOT_ACCEPTABLE)
        else:
            return Response({'detail':'verification code is valid'} , status=status.HTTP_200_OK)
    
    @action(detail=False, methods=['POST'])
    def get_verification_code(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user=serializer.validated_data['user']
        if user.is_banned:
            return Response({'detail':'this account is banned and cannot be accessed'}, status=status.HTTP_403_FORBIDDEN)
        try:
            user.send_code()
        except:
            return Response({'detail':'could not send OTP code,please try again'} , status=status.HTTP_502_BAD_GATEWAY)
        return Response({'detail':'verification code sent'} , status=status.HTTP_200_OK)
    
    @action(detail=False, methods=['POST'])
    def verification(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        validated_data=serializer.validated_data
        user = validated_data['user']
        if user.is_banned:
            return Response({'detail':'this account is banned and cannot be accessed'}, status=status.HTTP_403_FORBIDDEN)
        
        if validated_data['result']== 'expired':
            return Response({'detail':'verification code has expired'} , status=status.HTTP_406_NOT_ACCEPTABLE)
        elif validated_data['result'] == 'mismatch':
            return Response({'detail':'verification code does not match'} , status=status.HTTP_406_NOT_ACCEPTABLE)
        elif validated_data['result'] == 'verified':
            user.set_password(validated_data['password'])
            validated_data['user'].last_login = timezone.now()
            user.save()
            return Response(serializer.to_representation(user), status=status.HTTP_202_ACCEPTED)
            
        else:
            return Response({'detail':'verification failed for unknown reason'} , status=status.HTTP_417_EXPECTATION_FAILED)
        
        
        
class ProfileView(APIView):
    permission_classes = [IsAuthenticated]
    parser_classes = [MultiPartParser, FormParser]
    serializer_class = ProfileSerializer
    
    @swagger_auto_schema(
        operation_description="Retrieves the profile information of the currently authenticated user.",
        responses={200: ProfileSerializer()}
    )
    def get(self, request):
        if request.user.is_banned:
            return Response({'detail':'this account is banned and cannot be accessed'}, status=status.HTTP_403_FORBIDDEN)
        else:
            serializer = self.serializer_class(request.user)
            return Response(serializer.data)

    @swagger_auto_schema(
        operation_description="Updates the profile information of the currently authenticated user.",
        request_body=ProfileSerializer,
        responses={200: ProfileSerializer()}
    )
    def put(self, request, *args, **kwargs):
        if request.user.is_banned:
            return Response({'detail':'this account is banned and cannot be accessed'}, status=status.HTTP_403_FORBIDDEN)
        serializer = self.serializer_class(request.user, data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(serializer.data)

    @swagger_auto_schema(
        operation_description="Partially updates the profile information of the currently authenticated user.",
        request_body=ProfileSerializer,
        responses={200: ProfileSerializer()}
    )
    def patch(self, request, *args, **kwargs):
        if request.user.is_banned:
            return Response({'detail':'this account is banned and cannot be accessed'}, status=status.HTTP_403_FORBIDDEN)
        serializer = self.serializer_class(request.user, data=request.data, partial=True)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(serializer.data)
    
    
    
class ChangePasswordView(APIView):
    permission_classes = [IsAuthenticated]
    serializer_class = ChangePasswordSerializer

    @swagger_auto_schema(
        operation_description="change user password.",
        request_body=ChangePasswordSerializer,
        responses={200: ChangePasswordSerializer()} 
    )
    def patch(self, request):
        if request.user.is_banned:
            return Response({'detail':'this account is banned and cannot be accessed'}, status=status.HTTP_403_FORBIDDEN)
        serializer = self.serializer_class(data=request.data, context={'user': request.user})
        serializer.is_valid(raise_exception=True)
        password = serializer.validated_data['new_password']
        user = request.user
        user.set_password(password)
        user.save()
        return Response({'detail': 'password changed successfully'}, status=status.HTTP_200_OK)



class AdminViewSet(GenericViewSet):
    def get_permissions(self):
        if self.action in ['make_admin', 'make_superuser']:
            return [IsAuthenticated()]
        else:
            return [IsAuthenticated(),IsAdminUser()]
    
    def get_serializer_class(self):
        if self.action in ['make_admin', 'make_superuser']:
            return AdminPasswordSerializer
        else:
            return SelectUserSerializer
    
    @action(['POST'],detail=False)
    def make_admin(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        request.user.make_admin()
        return Response({'detail' : f'user {request.user.phone_number} now have Admin access'})
    
    @action(['POST'],detail=False)
    def make_superuser(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        request.user.make_superuser()
        return Response({'detail' : f'user {request.user.phone_number} now have Superuser access'})
    
    @action(['POST'],detail=False)
    def ban_user(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        user.ban()
        return Response({'detail' : f'user {user.phone_number} is now banned'})
    
    @action(['POST'],detail=False)
    def allow_user(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        user.unban()
        return Response({'detail' : f'removed restriction on user {user.phone_number}'})
    
    @action(['GET'],detail=False,url_path="user_info/(?P<number_or_email>[^/]+)")
    def user_info(self,request,number_or_email = None):
        # The serializer will handle the validation and NotFound error
        serializer = self.get_serializer(data={'number_or_email': number_or_email})
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        return Response(serializer.to_representation(user), status=status.HTTP_200_OK)
    
    @action(['GET'],detail=False)
    def all_users(self,request):
        all_users = User.objects.filter(is_staff = False)
        serializer = SelectUserSerializer(all_users, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)