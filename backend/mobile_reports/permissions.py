from rest_framework.permissions import BasePermission

class IsNotBanned(BasePermission):
    """
    Allows access only to users who are not banned.
    """
    message = "Your account has been banned. Please contact support."

    def has_permission(self, request, view):
        # Check if the user is authenticated and not banned
        return not request.user.is_banned