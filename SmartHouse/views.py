import json
from django.shortcuts import render
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from django.http import HttpResponse
from django.template import Context, loader

from django.shortcuts import render, get_object_or_404, redirect
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.contrib.auth import logout
from django.http import HttpResponseRedirect
from django.db import transaction
#from .forms import UserForm,ProfileForm
from google.oauth2 import id_token
from google.auth.transport import requests


@csrf_exempt
def index(request):
    
    print(request.method)
    print(request.path_info)
    #print(request.COOKIES)
    #print(request.META)
    #print(request.session)

    received_json_data=json.loads(request.body)
    tokenId = received_json_data['tokenId']
    try:
        idinfo = id_token.verify_oauth2_token(tokenId, requests.Request(), '804708077121-jlt5no06s0e33r1fp732ei8i4k4st5d1.apps.googleusercontent.com')
        print(idinfo)
    # idinfo = id_token.verify_oauth2_token(token, requests.Request())
    # if idinfo['aud'] not in [CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]:
    #     raise ValueError('Could not verify audience.')

      #  if idinfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
       #     raise ValueError('Wrong issuer.')

    # If auth request is from a G Suite domain:
    # if idinfo['hd'] != GSUITE_DOMAIN_NAME:
    #     raise ValueError('Wrong hosted domain.')

    # ID token is valid. Get the user's Google Account ID from the decoded token.
        #userid = idinfo['sub']
    except ValueError:
        # Invalid token
        pass
    response_data = {}
    response_data['result'] = 'error'
    response_data['message'] = 'Some error message'
    
    #return HttpResponse(response_data,content_type='application/json')
    #return "<a href="{% url 'social:begin' 'google-oauth2' %}">Entrar con la cuenta de Google</a>"
    return HttpResponse(json.dumps(response_data), content_type="application/json")
