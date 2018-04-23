from django.db import models
from django.contrib.auth.models import User
from django.db.models.signals import post_save
from django.dispatch import receiver



class Usuario(models.Model):
    Id_social = models.IntegerField(default=0)
    Nombre = models.CharField(max_length=200)
    Apellidos = models.CharField(max_length=200)
    Root = models.BooleanField(default = False)

    def __str__(self):
        return str(self.Id_social)


class Profile(models.Model):
    user = models.OneToOneField(User,unique=True, null=False, on_delete= models.CASCADE, db_index=True)
    
    bio = models.TextField(max_length=500, blank=True)
    location = models.CharField(max_length=30, blank=True)
    birth_date = models.DateField(null=True, blank=True)

    @receiver(post_save, sender=User)
    def create_user_profile(sender, instance, created, **kwargs):
        if created:
            Profile.objects.create(user=instance)

    @receiver(post_save, sender=User)
    def save_user_profile(sender, instance, **kwargs):
        instance.profile.save()
