from django.urls import path
from rest_framework.routers import DefaultRouter
from .views import *
from rest_framework_simplejwt.views import TokenRefreshView

router = DefaultRouter()
#router.register('', AuthenticationViewSet, 'auth')
#router.register("verification",VerificationViewSet,basename="verification")





urlpatterns = [
    path('refresh/', TokenRefreshView.as_view(), name='token_refresh'),
] + router.urls