from django.contrib.auth.models import User
from .serializers import *
from .models import *
from rest_framework.response import Response
from rest_framework.permissions import AllowAny, IsAuthenticated, IsAdminUser
from rest_framework.viewsets import ModelViewSet , GenericViewSet , mixins 
class AuthenticationViewSet(GenericViewSet):
    def get_serializer_class(self):
        if self.action == 'register':
            return RegisterSerializer
        elif self.action == 'login':
            return LoginSerializer
        elif self.action=="verification":
            return VerificationSerializer
    permission_classes = [AllowAny]
    
    
class ProfileViewSet(ModelViewSet):
#    def get_queryset(self):
#        return User.objects.filter(id=self.request.user.id)
    permission_classes = [IsAuthenticated]
    lookup_field = "phone_number"
    serializer_class = ProfileSerializer
