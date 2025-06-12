from django.urls import path
from rest_framework.routers import DefaultRouter
from .views import *
from rest_framework_simplejwt.views import TokenRefreshView

router = DefaultRouter()
router.register('', AuthenticationViewSet, 'auth')
router.register('admin',AdminViewSet,'adimn')



urlpatterns = [
    path('refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    path('profile/', ProfileView.as_view(), name='profile'),
    path('profile/change_password', ChangePasswordView.as_view(), name='change_password')
] + router.urls