import requests
import json

BASE_URL = "http://localhost:8080" # Gateway if available, or services individually
USER_SERVICE = "http://localhost:8085"
CATALOG_SERVICE = "http://localhost:8082"
SEAT_SERVICE = "http://localhost:8083"

def register_admin():
    print("Registering Admin User...")
    payload = {
        "username": "admin",
        "password": "password",
        "email": "admin@cinema.com",
        "role": "ADMIN" 
    }
    try:
        # First try login to see if exists
        login_payload = {"email": "superadmin@cinema.com", "password": "password"}
        res = requests.post(f"{USER_SERVICE}/auth/login", json=login_payload)
        if res.status_code == 200:
            print("Admin already exists.")
            return res.json()["token"]
    except:
        pass

    try:
        # Update payload to use superadmin
        payload["username"] = "superadmin"
        payload["email"] = "superadmin@cinema.com"
        
        res = requests.post(f"{USER_SERVICE}/auth/register", json=payload)
        if res.status_code == 200:
            print("Admin registered.")
            # Login to get token
            res = requests.post(f"{USER_SERVICE}/auth/login", json={"email": "superadmin@cinema.com", "password": "password"})
            return res.json()["token"]
        else:
            print(f"Registration failed: {res.text}")
            return None
    except Exception as e:
        print(f"Error: {e}")
        return None

def create_movie(token):
    print("Creating Movie...")
    headers = {"Authorization": f"Bearer {token}"}
    payload = {
        "title": "Inception",
        "director": "Christopher Nolan",
        "releaseYear": 2010,
        "genre": "Sci-Fi"
    }
    try:
        res = requests.post(f"{CATALOG_SERVICE}/movies", json=payload, headers=headers)
        if res.status_code in [200, 201]:
            print("Movie created.")
            return res.json()["id"]
        else:
            print(f"Movie creation failed: {res.text}")
            return None
    except Exception as e:
        print(f"Error: {e}")
        return None

def create_showtime(token, movie_id):
    print("Creating Showtime...")
    headers = {"Authorization": f"Bearer {token}"}
    payload = {
        "movieId": movie_id,
        "startTime": "2023-12-25T20:00:00",
        "hallName": "IMAX Hall 1",
        "totalSeats": 50
    }
    try:
        res = requests.post(f"{CATALOG_SERVICE}/showtimes", json=payload, headers=headers)
        if res.status_code in [200, 201]:
            print("Showtime created.")
            return res.json()["id"]
        else:
            print(f"Showtime creation failed: {res.text}")
            return None
    except Exception as e:
        print(f"Error: {e}")
        return None

def init_seats(token, showtime_id):
    print(f"Initializing Seats for Showtime {showtime_id}...")
    headers = {"Authorization": f"Bearer {token}"}
    try:
        res = requests.post(f"{SEAT_SERVICE}/seats/init/{showtime_id}?count=50", headers=headers)
        if res.status_code in [200, 201]:
            print("Seats initialized.")
        else:
            print(f"Seat initialization failed: {res.text}")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    token = register_admin()
    if token:
        print(f"Got Token: {token}")
        movie_id = create_movie(token)
        if movie_id:
            showtime_id = create_showtime(token, movie_id)
            if showtime_id:
                init_seats(token, showtime_id)
        else:
            # Maybe movie already exists? list them
            pass
    else:
        print("Could not get token.")
