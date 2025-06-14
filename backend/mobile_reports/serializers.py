from rest_framework import serializers
from .models import *

class ProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ["phone_number","email","username","image","is_staff","is_banned","date_joined","allow_admin_access"]
        read_only_fields = ["phone_number","email","username","image","date_joined","allow_admin_access"]
        
        
        
class DeviceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Device
        fields = '__all__'
        read_only_fields = ['id','created_at', 'last_seen']



class MeasurementSerializer(serializers.ModelSerializer):
    class Meta:
        model = Measurement
        fields = '__all__'
        read_only_fields = ['id','user','created_at']



class TestResultSerializer(serializers.ModelSerializer):
    class Meta:
        model = TestResult
        fields = '__all__'
        read_only_fields = ['id', 'user','created_at']
        
        
        
class BulkMeasurementSerializer(serializers.ModelSerializer):
    class Meta:
        model = Measurement
        exclude = ['user']
        read_only_fields = ['id','created_at']
        
        
        
class BulkTestResultSerializer(serializers.ModelSerializer):
    class Meta:
        model = TestResult
        exclude = ['user']
        read_only_fields = ['id','created_at']
        

        
class BulkUploadMeasurementSerializer(serializers.Serializer):
    measurements = serializers.ListField(child=BulkMeasurementSerializer())

    def create(self, validated_data):
        created_measurement = []

        
        for measurement_data in validated_data['measurements']:
            measurement = Measurement.objects.create(user=self.context['user'],**measurement_data)
            created_measurement.append(measurement)
        return created_measurement
    


class BulkUploadTestResultSerializer(serializers.Serializer):
    test_results = serializers.ListField(child=BulkTestResultSerializer())
    def create(self, validated_data):
        created_test_result = []
        
        for test_data in validated_data['test_results']:
            test_result = TestResult.objects.create(user=self.context['user'],**test_data)
            created_test_result.append(test_result)
        return created_test_result
    
    
    
class BulkDeleteSerializer(serializers.Serializer):
    ids = serializers.ListField(child=serializers.IntegerField())