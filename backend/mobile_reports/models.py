from django.db import models
from django.contrib.auth import get_user_model
from .utils import *
User = get_user_model()



class Device(models.Model):
    device_id = models.CharField(max_length=255, primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='devices')
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
    
    device = models.ForeignKey(Device, on_delete=models.CASCADE, related_name='measurements')
    timestamp = models.DateTimeField(null=True, blank=True)
    latitude = models.FloatField()
    longitude = models.FloatField()
    network_type = models.CharField(max_length=10, choices=NETWORK_TYPES)
    signal_strength = models.FloatField(null=True, blank=True)  # RSRP or RSCP or RxLev 
    signal_quality = models.FloatField(null=True, blank=True)  # RSRQ or Ec/N0
    cell_id = models.CharField(max_length=100, null=True, blank=True)
    lac = models.CharField(max_length=100, null=True, blank=True)  # Location Area Code
    tac = models.CharField(max_length=100, null=True, blank=True)  # Tracking Area Code
    rac = models.CharField(max_length=100, null=True, blank=True)  # routing Area Code
    plmn = models.CharField(max_length=100, null=True, blank=True) # Public Land Mobile Network
    arfcn = models.IntegerField(null=True, blank=True)  # Absolute Radio Frequency Channel Number
    created_at = models.DateTimeField(auto_now_add=True)
    
    @property
    def arfcn_frequency(self):
        return arfcn_to_frequency(self.arfcn, self.network_type)

    class Meta:
        ordering = ['-timestamp']
        indexes = [
            models.Index(fields=['device', 'timestamp']),
            models.Index(fields=['latitude', 'longitude']),
        ]
        
        
    def __str__(self):
        return f"Measurement at {self.timestamp} by {self.device}"



class TestResult(models.Model):
    TEST_TYPES = {
        'HTTPD': 'HTTPD',   #HTTP Download
        'HTTPU': 'HTTPU',   #HTTP Upload  
        'PING' : 'PING' ,
        'WEB'  : 'WEB'  ,
        'DNS'  : 'DNS'  ,
        'SMS'  : 'SMS'  ,
    }
    device = models.ForeignKey(Device, on_delete=models.CASCADE, related_name='test_results')
    timestamp = models.DateTimeField(null=True, blank=True)
    test_type = models.CharField(max_length=10, choices=TEST_TYPES)
    value = models.FloatField()  
    success = models.BooleanField()
    additional_info = models.JSONField(null=True, blank=True) 
    created_at = models.DateTimeField(auto_now_add=True)
    class Meta:
        ordering = ['-timestamp']
        indexes = [
            models.Index(fields=['device', 'timestamp']),
            ]
    
    def __str__(self):
        return f"{self.get_test_type_display()} Test - {self.value}"