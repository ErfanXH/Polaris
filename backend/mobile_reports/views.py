from django.contrib.auth import get_user_model
from .serializers import *
from .models import *
from rest_framework import status
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated , IsAdminUser
from rest_framework.viewsets import GenericViewSet , ModelViewSet , mixins
from rest_framework.decorators import action

User = get_user_model()

class DeviceViewSet(ModelViewSet):
    def get_queryset(self):
        return self.request.user.devices.all()
        
    serializer_class =DeviceSerializer
    permission_classes = [IsAuthenticated]
    lookup_field = 'device_id'

    
    def perform_create(self, serializer):
        serializer.save(user=self.request.user)



class MeasurementViewSet(mixins.CreateModelMixin,
                   mixins.RetrieveModelMixin,
                   mixins.DestroyModelMixin,
                   mixins.ListModelMixin,
                   GenericViewSet):
    def get_queryset(self):
        device_id = self.request.query_params.get('device_id')
        if device_id:
            return Measurement.objects.filter(device__user=self.request.user, device__device_id=device_id)
        return Measurement.objects.filter(device__user=self.request.user)
    serializer_class = MeasurementSerializer
    permission_classes = [IsAuthenticated]



class TestResultViewSet(mixins.CreateModelMixin,
                   mixins.RetrieveModelMixin,
                   mixins.DestroyModelMixin,
                   mixins.ListModelMixin,
                   GenericViewSet):
    def get_queryset(self):
        device_id = self.request.query_params.get('device_id')
        if device_id:
            return TestResult.objects.filter(device__user=self.request.user, device__device_id=device_id)
        return TestResult.objects.filter(device__user=self.request.user)
    serializer_class = TestResultSerializer
    permission_classes = [IsAuthenticated]
    
    
    
class BulkUploadViewSet(GenericViewSet):
    def get_serializer_class(self):
        if self.action == 'measurement':
            return BulkUploadMeasurementSerializer
        elif self.action == 'test_report':
            return BulkUploadTestResultSerializer
    
    permission_classes = [IsAuthenticated]
    
    @action(methods=['POST'],detail=False)
    def measurement(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        result = serializer.save()
        return Response({'detail':f'{len(result)} measurement reports has successfully created'},status=status.HTTP_201_CREATED)

    @action(methods=['POST'],detail=False)
    def test_report(self,request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        result = serializer.save()
        return Response({'detail':f'{len(result)} test reports has successfully created'},status=status.HTTP_201_CREATED) 
    