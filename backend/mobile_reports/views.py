from django.contrib.auth import get_user_model
from .serializers import *
from .models import *
from rest_framework import status
from rest_framework import serializers
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.permissions import IsAuthenticated , IsAdminUser
from rest_framework.viewsets import GenericViewSet , ModelViewSet , mixins
from rest_framework.exceptions import ValidationError
from rest_framework.decorators import action
from random import randbytes

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
    
    def get_queryset(self,device_id = None):
        device_id = self.kwargs.get('device_id') or device_id
        if device_id:
            return Measurement.objects.filter(device__user=self.request.user, device__device_id=device_id)
        else:
            raise ValidationError('device id must be given via query params')
        
    serializer_class = MeasurementSerializer
    permission_classes = [IsAuthenticated]
    
    def perform_create(self, serializer, device_id = None):
        device_id = self.kwargs.get('device_id') or device_id
        serializer.save(device = Device.objects.get(device_id = device_id))
        
    @action(methods=['GET'],detail=False)
    def latest(self, request,device_id =None):
        instance = self.get_queryset().latest('timestamp')
        serializer = self.get_serializer(instance)
        return Response(serializer.data)



class TestResultViewSet(mixins.CreateModelMixin,
                   mixins.RetrieveModelMixin,
                   mixins.DestroyModelMixin,
                   mixins.ListModelMixin,
                   GenericViewSet):
    
    def get_queryset(self):
        device_id = self.kwargs.get('device_id') or device_id
        if device_id:
            return TestResult.objects.filter(device__user=self.request.user, device__device_id=device_id)
        else:
            raise ValidationError('device id must be given via query params')
        
    serializer_class = TestResultSerializer
    permission_classes = [IsAuthenticated]
    
    def perform_create(self, serializer,device_id = None):
        device_id = self.kwargs.get('device_id') or device_id
        serializer.save(device = Device.objects.get(device_id = device_id))
        
    @action(methods=['GET'],detail=False)
    def latest(self, request,device_id =None):
        instance = self.get_queryset().latest('timestamp')
        serializer = self.get_serializer(instance)
        return Response(serializer.data)
    
    
    
class BulkUploadViewSet(GenericViewSet):
    
    def get_serializer_class(self):
        if self.action == 'measurement':
            return BulkUploadMeasurementSerializer
        elif self.action == 'test_report':
            return BulkUploadTestResultSerializer
    
    permission_classes = [IsAuthenticated]
    
    @action(methods=['POST'],detail=False)
    def measurement(self,request, device_id=None):
        device_id = self.kwargs.get('device_id') or device_id
        if not device_id:
            raise ValidationError('device id must be given via query params')
        serializer = self.get_serializer(data=request.data ,context={"device_id": device_id})
        serializer.is_valid(raise_exception=True)
        result = serializer.save()
        return Response({'detail':f'{len(result)} measurement reports has successfully created'},status=status.HTTP_201_CREATED)

    @action(methods=['POST'],detail=False)
    def test_report(self,request, device_id=None):
        device_id = self.kwargs.get('device_id') or device_id
        if not device_id:
            raise ValidationError('device id must be given via query params')
        serializer = self.get_serializer(data=request.data,context={"device_id": device_id})
        serializer.is_valid(raise_exception=True)
        result = serializer.save()
        return Response({'detail':f'{len(result)} test reports has successfully created'},status=status.HTTP_201_CREATED) 
    


class BulkDeleteViewSet(GenericViewSet):
    
    permission_classes = [IsAuthenticated]
    serializer_class = BulkDeleteSerializer
    
    @action(methods=['POST'],detail=False)
    def measurement(self,request, device_id=None):
        device_id = self.kwargs.get('device_id') or device_id
        if not device_id:
            raise ValidationError('device id must be given via query params')
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        validated_data = serializer.validated_data
        try:
            delete_count,_ = Measurement.objects.filter(device__device_id = device_id,
                                                        id__in = validated_data['ids']).delete()
        except: #set a good except for it
            return Response({'detail':f'error while deleting,{delete_count} records deleted'},status=status.HTTP_417_EXPECTATION_FAILED)

        return Response({'detail':f'{delete_count} measurement reports has successfully deleted'},status=status.HTTP_200_OK)

    @action(methods=['POST'],detail=False)
    def test_report(self,request, device_id=None):
        device_id = self.kwargs.get('device_id') or device_id
        if not device_id:
            raise ValidationError('device id must be given via query params')
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        validated_data = serializer.validated_data
        try:
            delete_count,_ = TestResult.objects.filter(device__device_id = device_id
                                                       ,id__in = validated_data['ids']).delete()
        except: #set a goo except for it
            return Response({'detail':f'error while deleting,{delete_count} records deleted'},status=status.HTTP_417_EXPECTATION_FAILED)

        return Response({'detail':f'{delete_count} measurement reports has successfully deleted'},status=status.HTTP_200_OK)
    


class HTTPTestViewSet(GenericViewSet):
    
    def get_queryset(self):
        return TestResult.objects.none()
    
    permission_classes = [IsAuthenticated]
    
    @action(methods=['GET'],detail=False)
    def download(self,request,device_id = None):
        return Response(data=str(randbytes(2**19)),status=status.HTTP_200_OK)
        
    @action(methods=['POST'],detail=False)
    def upload(self,request,device_id = None):
        return Response(status=status.HTTP_204_NO_CONTENT)
