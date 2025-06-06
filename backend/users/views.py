from django.contrib.auth.models import User
from .serializers import *
from .models import *
from rest_framework import status
from rest_framework.response import Response
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.viewsets import ModelViewSet , GenericViewSet
from rest_framework.decorators import action
class AuthenticationViewSet(GenericViewSet):
    def get_serializer_class(self):
        if self.action == 'register':
            return RegisterSerializer
        elif self.action == 'login':
            return LoginSerializer
        elif self.action=="verify_code":
            return VerifyCodeSerializer
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
            user = User.objects.create(
                phone_number=validated_data['phone_number'],
                email=validated_data['email'],
                password=validated_data['password'])
        else:
            auth_status['register_state'] = 'existed'
            user = validated_data['user']
            
        try:
            user.send_code()
        except:
            return Response('could not send OTP code,please try again' , status=status.HTTP_502_BAD_GATEWAY)
        
        auth_status['message'] = 'OTP code has been sent'
        return Response(auth_status, status=status.HTTP_201_CREATED)
    
    @action(detail=False, methods=['POST'])
    def login(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        validated_data=serializer.validated_data
        if validated_data['verified']:
            return Response(serializer.to_representation(validated_data['user']), status=status.HTTP_202_ACCEPTED)
        else:
            return Response({'message' : 'verification required, verify with code sent to your email'}, status=status.HTTP_403_FORBIDDEN)

    @action(detail=False, methods=['GET','POST'] , url_path="verify_code/(?P<number_or_email>\d+)")
    def verify_code(self,request):
        if request.method == 'GET':
            number_or_email = request.query_params.get('number_or_email')
            serializer = self.get_serializer(data={'number_or_email' : number_or_email})
            serializer.is_valid(raise_exception=True)
            validated_data=serializer.validated_data
            try:
                validated_data['user'].send_code()
            except:
                return Response({'message':'could not send OTP code,please try again'} , status=status.HTTP_502_BAD_GATEWAY)
            return Response({'message':'verification code sent'} , status=status.HTTP_200_OK)
        else:
            serializer = self.get_serializer(data=request.data)
            serializer.is_valid(raise_exception=True)
            validated_data=serializer.validated_data
            if not validated_data.get('code'):
                return Response({'message':'code field should not be empty'} , status=status.HTTP_400_BAD_REQUEST)
            if validated_data['user'].verify(validated_data['code'],confirm = False):
                return Response({{'message':'verification code is valid'}} , status=status.HTTP_204_NO_CONTENT)
            else:
                return Response({{'message':'verification code does not match'}} , status=status.HTTP_406_NOT_ACCEPTABLE)
            
    @action(detail=False, methods=['POST'])
    def verification(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        validated_data=serializer.validated_data
        return Response(serializer.to_representation(validated_data['user']), status=status.HTTP_202_ACCEPTED)
        
class ProfileViewSet(ModelViewSet):
    def get_queryset(self):
        return User.objects.filter(phone_number=self.request.user.phone_number)
    permission_classes = [IsAuthenticated]
    lookup_field = "phone_number"
    serializer_class = ProfileSerializer
