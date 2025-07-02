from django.db import models
from django.contrib.auth import get_user_model
from .utils import *
User = get_user_model()



class Device(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='devices')
    device_id = models.CharField(max_length=255,primary_key=True)
    manufacturer = models.CharField(max_length=100)
    model = models.CharField(max_length=100)
    os_version = models.CharField(max_length=50)
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    last_seen = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.manufacturer} {self.model} ({self.user.phone_number})"



class Measurement(models.Model):
    NETWORK_TYPES = {
        'GSM'    : 'GSM'    ,
        'GPRS'   : 'GPRS'   ,
        'EDGE'   : 'EDGE'   ,
        'UMTS'   : 'UMTS'   ,
        'HSPA'   : 'HSPA'   ,
        'HSPA+'  : 'HSPA+'  ,
        'LTE'    : 'LTE'    ,
        '5G'     : '5G'     ,
        'LTE-Adv': 'LTE-Adv',
        'UNKNOWN': 'UNKNOWN',
    }
    
    NETWORK_TYPES_MAP = {v: k for k, v in NETWORK_TYPES.items()}
    
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='user_measurements')
    latitude = models.FloatField()
    longitude = models.FloatField()
    timestamp = models.DateTimeField(null=True, blank=True,unique=True)
    network_type = models.CharField(max_length=10, choices=NETWORK_TYPES)
    lac = models.CharField(max_length=100, null=True, blank=True)  # Location Area Code
    tac = models.CharField(max_length=100, null=True, blank=True)  # Tracking Area Code
    rac = models.CharField(max_length=100, null=True, blank=True)  # routing Area Code
    cell_id = models.CharField(max_length=100, null=True, blank=True)
    plmn_id = models.CharField(max_length=100, null=True, blank=True) # Public Land Mobile Network
    arfcn = models.IntegerField(null=True, blank=True)  # Absolute Radio Frequency Channel Number
    frequency = models.FloatField(null=True, blank=True)
    frequency_band = models.CharField(max_length=100, null=True, blank=True)
    rsrp = models.IntegerField(null=True, blank=True)
    rsrq = models.IntegerField(null=True, blank=True)
    rscp = models.IntegerField(null=True, blank=True)
    ecIo = models.IntegerField(null=True, blank=True)
    rxLev = models.IntegerField(null=True, blank=True)
    ssRsrp = models.IntegerField(null=True, blank=True)
    http_upload = models.FloatField()
    http_download = models.FloatField()
    ping_time = models.FloatField()
    dns_response = models.IntegerField()
    web_response = models.BigIntegerField()
    sms_delivery_time = models.IntegerField()
    created_at = models.DateTimeField(auto_now_add=True)
    class Meta:
        ordering = ['-timestamp']
        indexes = [
            models.Index(fields=['user', 'timestamp']),
            models.Index(fields=['latitude', 'longitude']),
        ]
        
        
    def __str__(self):
        return f"Measurement at {self.timestamp} by {self.user}"



class TestResult(models.Model):
    TEST_TYPES = {
        'HTTPD': 'HTTPD',   #HTTP Download
        'HTTPU': 'HTTPU',   #HTTP Upload  
        'PING' : 'PING' ,
        'WEB'  : 'WEB'  ,
        'DNS'  : 'DNS'  ,
        'SMS'  : 'SMS'  ,
    }
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='users_testResults')
    timestamp = models.DateTimeField(null=True, blank=True,unique=True)
    test_type = models.CharField(max_length=10, choices=TEST_TYPES)
    value = models.FloatField()  
    success = models.BooleanField()
    additional_info = models.JSONField(null=True, blank=True) 
    created_at = models.DateTimeField(auto_now_add=True)
    class Meta:
        ordering = ['-timestamp']
        indexes = [
            models.Index(fields=['user', 'timestamp']),
            ]
    
    def __str__(self):
        return f"{self.get_test_type_display()} Test - {self.value}"