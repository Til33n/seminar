import uvicorn

from fastapi import FastAPI
from fastapi.responses import FileResponse
from database_main import show_all, specific_data_lookup, username_lookup
from database_main import show_all_data, show_user_data,show_all_usernames, show_user_matches, show_all_scores_chart, add_data, add_player_score, update_user, update_user_score,update_user_stats, delete_user, update_user_score
from pydantic import BaseModel
from fastapi.encoders import jsonable_encoder


app = FastAPI()

######  GET  #######
@app.get("/")   # MAIN  index  PAGE     
async def root():
    return FileResponse('index.html', media_type='text/html')
    

@app.get("/login")   # LOGIN 
async def login(user: str = "",
                password: str = ""):
    if(username_lookup("database", "users", str(user))):
        if(user == specific_data_lookup("database", "users","username",1,user)  and  password == specific_data_lookup("database","users","username",2,user)):      
            return FileResponse('game_stats.html', media_type='text/html')
        else:
            return FileResponse('index.html', media_type='text/html')
    else:
        return FileResponse('index.html', media_type='text/html')
   


@app.get("/show_all_usernames")  # FETCH PLAYER SCORES
async def load_stats():
    user = show_all_usernames("database")
    return user



@app.get("/delete")   # ADMIN LOGIN and USER DELETE
async def admin_login(admin: str = "",
                      password: str = "",
                      delete_u: str = ""):
    if(username_lookup("exec_database", "admins", str(admin))):
        #return {"STATUS": "1"}
        if(admin == specific_data_lookup("exec_database", "admins","username",1,admin)  and  password == specific_data_lookup("exec_database","admins","username",2,admin)): 
            delete_user("database", "users", delete_u)
            return {"STATUS": "User deleted successfully"}
        else:
            return {"STATUS": "User or credentials iINCORRECT"}
    else:
        return {"STATUS": "credentials iINCORRECT"}



@app.get("/user_update")   # UPDATE USER SETTINGS 
async def user_update(new_email: str = "",
                      new_password: str="",
                      curr_user: str = "",
                      curr_password: str = ""):
    if(username_lookup("database", "users", str(curr_user))):
        if(curr_user == specific_data_lookup("database", "users","username",1,curr_user)  and  curr_password == specific_data_lookup("database","users","username",2,curr_user)):
            update_user_stats("database", curr_user, new_password, new_email)
            return {"STATUS": "User settings updated successfully"}
        else:
            return {"STATUS": "Username and password INCORRECT"}
    else:
        return {"STATUS": "Username INCORRECT"}

            

@app.get("/score_reset/{input}")   # SCORE RESET 
async def score_reset(input:str):
    username = input
    highest_score = 0
    played_matches= 0
    delete_user("database", "player_scores", username)
    update_user_score("database", username, highest_score, played_matches)  
    return {"STATUS": "Score reseted successfully"}




@app.get("/game")  # STATS PAGE
async def load_stats():  
    return {"STATUS": "File upload successfully and download URL sent to recepients EMAIL adress"}


@app.get("/all_users")
async def load_stats():
    users = show_all_data("database", "users")
    return users

@app.get("/all_users/{input}")
async def load_user_stats(input:str):
    user = input
    user = show_user_data("database","users",str(user))
    return user

@app.get("/played_rounds/{input}")
async def load_user_stats(input:str):
    user = input
    user_matches = show_user_matches("database","played_rounds",str(user))
    return user_matches


@app.get("/scores")
async def show_scores():
    scores = show_all_scores_chart("database","player_scores")
    return scores






######  POST  #######
class Item_1(BaseModel):
    username:       str
    password:       str
    email:          str
    highest_score:  int
    played_matches: int
@app.post("/add_user")
def handle_json_1(item: Item_1):
    item = jsonable_encoder(item)
    username =       item["username"]
    password =       item["password"]
    email =          item["email"]
    highest_score =  item["highest_score"]
    played_matches = item["played_matches"]
    add_data("database","users", username , password, email, highest_score, played_matches)
    return {"STATUS": "user data received"}



class Item_2(BaseModel):
    username:       str
    highest_score:  int
    time_played:    int
@app.post("/add_match")
def handle_json_2(item: Item_2):
    item = jsonable_encoder(item)
    username =      item["username"]
    highest_score = item["highest_score"]
    time_played =   item["time_played"]
    add_player_score("database","player_scores", username, highest_score, time_played)
    return {"STATUS": "match data received"}





######  PUT  #######
class Item_3(BaseModel):
    password:       str
    email:          str
    highest_score:  int
    played_matches: int
#@app.put("/update_user/{input}")
@app.put("{input}")
def handle_json_3(item: Item_3, input: str =""):
    item = jsonable_encoder(item)
    username =      input
    password  =     item["password"]
    email =         item["email"]
    highest_score = item["highest_score"]
    played_matches =   item["played_matches"]
    update_user("database", username, password ,email ,highest_score ,played_matches)
    return {"STATUS": "user update received"}






######  PUT  #######
class Item_3(BaseModel):
    user:   str
    score:  int
@app.put("{input}")
def handle_json_3(item: Item_3, input: str =""):
    item = jsonable_encoder(item)
    user =  item["user"]
    score = item["score"]
    played_matches = 0
    update_user_score("database", user, score, played_matches)  
    return {"STATUS": "user score updated"}






######  DELETE  #######
#@app.delete("/remove/{input}")
@app.delete("{input}")
async def delete_record(input: str = ""):
    user = input
    delete_user("database", "users", str(user))
    return{"STATUS": "user removed from users"}



if __name__ == "__main__":
    uvicorn.run(app, host="192.168.64.18", port=5000)
    app.run(debug = True)
