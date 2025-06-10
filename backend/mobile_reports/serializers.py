from rest_framework import serializers
from .models import *

class ProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ["phone_number","email","username","image","is_staff","is_banned","date_joined","allow_usperuser_access"]
        read_only_fields = ["phone_number","email","username","image","is_staff","is_banned","date_joined","allow_usperuser_access"]
        
        
        
class DeviceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Device
        fields = ['device_id', 'manufacturer', 'model', 'os_version', 'is_active', 'created_at', 'last_seen']
        read_only_fields = ['created_at', 'last_seen']



class MeasurementSerializer(serializers.ModelSerializer):
    class Meta:
        model = Measurement
        fields = '__all__'
        read_only_fields = ['id', 'timestamp','device']



class TestResultSerializer(serializers.ModelSerializer):
    class Meta:
        model = TestResult
        fields = '__all__'
        read_only_fields = ['id', 'timestamp','device']
        
        
        
class BulkMeasurementSerializer(serializers.ModelSerializer):
    class Meta:
        model = Measurement
        exclude = ['device']
        read_only_fields = ['id', 'timestamp']
        
        
        
class BulkTestResultSerializer(serializers.ModelSerializer):
    class Meta:
        model = TestResult
        exclude = ['device']
        read_only_fields = ['id', 'timestamp']
        

        
class BulkUploadMeasurementSerializer(serializers.Serializer):
    measurements = serializers.ListField(child=BulkMeasurementSerializer())

    def create(self, validated_data):
        device = Device.objects.get(device_id=self.context['device_id'])
        measurements = []
        
        for measurement_data in validated_data['measurements']:
            measurement = Measurement.objects.create(
                device=device,
                latitude=measurement_data.get('latitude'),
                longitude=measurement_data.get('longitude'),
                network_type=measurement_data.get('network_type'),
                signal_strength=measurement_data.get('signal_strength'),
                signal_quality=measurement_data.get('signal_quality'),
                cell_id=measurement_data.get('cell_id'),
                lac=measurement_data.get('lac'),
                tac=measurement_data.get('tac'),
                plmn=measurement_data.get('plmn'),
                arfcn=measurement_data.get('arfcn'),
            )
            measurements.append(measurement)
        return measurements
    


class BulkUploadTestResultSerializer(serializers.Serializer):
    test_results = serializers.ListField(child=BulkTestResultSerializer())
    
    def create(self, validated_data):
        device = Device.objects.get(device_id=self.context['device_id'])
        test_results = []
        
        for test_data in validated_data['test_results']:
            test_result = TestResult.objects.create(
                device=device,
                test_type=test_data.get('test_type'),
                value=test_data.get('value'),
                success=test_data.get('success'),
                additional_info=test_data.get('additional_info'),
            )
            test_results.append(test_result)
    
        return test_results
    
    
    
class BulkDeleteSerializer(serializers.Serializer):
    ids = serializers.ListField(child=serializers.IntegerField())