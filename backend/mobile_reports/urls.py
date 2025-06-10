from django.urls import path
from rest_framework.routers import DefaultRouter
from .views import *

router = DefaultRouter()
router.register('device', DeviceViewSet, 'device')
router.register(r'device/(?P<device_id>\d+)/measurement', MeasurementViewSet, 'measurement')
router.register(r'device/(?P<device_id>\d+)/test_result', TestResultViewSet, 'test_result')
router.register(r'device/(?P<device_id>\d+)/bulk_upload', BulkUploadViewSet, 'bulk_upload')
router.register(r'device/(?P<device_id>\d+)/bulk_delete', BulkDeleteViewSet, 'bulk_delete')


urlpatterns = [

] + router.urls