from django.urls import path
from rest_framework.routers import DefaultRouter
from .views import *

router = DefaultRouter()
#router.register('device', DeviceViewSet, 'device')
router.register('measurement', MeasurementViewSet, 'measurement')
router.register('test_result', TestResultViewSet, 'test_result')
router.register('bulk_upload', BulkUploadViewSet, 'bulk_upload')
router.register('bulk_delete', BulkDeleteViewSet, 'bulk_delete')
router.register('HTTPTest', HTTPTestViewSet, 'HTTPTest')

urlpatterns = [

] + router.urls