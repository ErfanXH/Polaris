from rest_framework import permissions
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from django.conf import settings
from rest_framework_simplejwt.authentication import JWTAuthentication

schema_view = get_schema_view(
   openapi.Info(
      title="Net Watchers polaris Project",
      default_version="v1",
      description="Polaris project,created by Net Watcher team for mobile network cell visualization",
      terms_of_service=None,
      contact=openapi.Contact(email="www.hmdsdt@gmail.com"),
      license=openapi.License(name="MIT License"),
      x={
         'security': [{'Bearer': []}],
      },
   ),
   public=True,
   permission_classes=(permissions.AllowAny,),
   authentication_classes=(JWTAuthentication,),
)

