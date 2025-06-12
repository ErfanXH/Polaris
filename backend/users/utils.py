from datetime import datetime
from django.utils import timezone
from django.conf import settings
from requests import post
from uuid import uuid4
import os

def send_OTP(destiantion_numbers: list|str ,message: str ,code_length : int = 5) -> str:
    url = 'https://portal.amootsms.com/rest/SendQuickOTP'
    line_number = 'public'
    #token = '1531E805B6AB23BD8CBD90C448B597FC2B494E41'
    token = settings.SMS_API_TOKEN
    #time  = str(datetime.now())
    time = str(timezone.now())
    destiantion_numbers = str(destiantion_numbers) if type(destiantion_numbers) == list else str(list((destiantion_numbers,)))
    data = {
        'Token' : token,
        'SendDateTime' : time,
        'SMSMessageText' : message,
        'LineNumber' : line_number,
        'Mobile' : destiantion_numbers,
        'CodeLength' : code_length
    }
    response = post(url=url , data=data)
    return response.json()



def name_generator():    
    return uuid4().hex[:30]
    
def file_name(instance, filename):
    ext = filename.split('.')[-1]
    filename = f'{uuid4().hex}.{ext}'
    return os.path.join("profile_images/", filename)
    
if __name__  == '__main__':
    response = send_OTP('9360904088', 'این یک پیام تست است\nاینم بخش دیگه تست')
    print(response)
    