Projekt je sestavljen iz treh glavnih delov. Ti deli morajo delovati posamično, kot tudi integritetno, 
da je pričakovan rezultat projekta (mobilna igra) uporaben in praktičen.

1.) back-end 
Zaledni del je sestavljen iz "server.py" in "database_main.py". V prvei datoteki imamo deklarirane glavne API klice in sicer GET, POST, PUT in DELETE. Druga datoteka 

1.1.) back_end - server.py
    1.1.1.)Prvi API se uporabi za nalaganje osnovne spletne strani za ragistracijo/vpis uporabnikov. Na prazni IP naslov (v našem primeru                     http://93.103.156.225) vrne datoteko index.html (glej poglavje 2.1)
   
    ######  GET  #######
    @app.get("/")   # MAIN  index  PAGE     
    async def root():
        return FileResponse('index.html', media_type='text/html')
   
1.1.2.)Drugi API se pa uporabi za preverjanje verodostojnosti podatkov. Na spletni strani index.html, uporabnik vnese svoje uporabniško ime in            geslo. Z pritiskom gumba (Submit) se podatki preko URL-ja pošljejo na spodaj podan API. API sproži set funkcij, ki pošljejo poizvedbo v            podatkovno bazo prvo se preveri če uporabnik z podanim uporabniškim imenom obstaja, če ja, preveri še geslo. Če sta obe poizvedbi                  "True", potem API uporabnika preusmeri na spletno stran "game_stats.html" 

    ######  GET  #######
    @app.get("/login")   # LOGIN 
    async def login(user: str = "",
                password: str = ""):
    if(username_lookup("database", "users", str(user))):
        if(user == specific_data_lookup("database", "users","username",1,user)  and  password ==                                          specific_data_lookup("database","users","username",2,user)):      
            return FileResponse('game_stats.html', media_type='text/html')
        else:
            return FileResponse('index.html', media_type='text/html')
    else:
        return FileResponse('index.html', media_type='text/html')
        

1.1.3.) Tretji API se uporablja izključno za poizvedbo vseh trenutno registriranih uporabnikov spletne strani. Vrne nam uporabniška imena v obliki JSON. Ta poizvedba se uporablja za namen brisanja uporabnikov. Brisanje zapisov/uporabnikov iz podatkovne baze je omogočeno izključno administratorju spletne strani. Ta se mora naknadno prijaviti (glej sklop 1.1.4.)
     
      ######  GET  ######
      @app.get("/show_all_usernames")  # FETCH PLAYER SCORES
      async def load_stats():
          user = show_all_usernames("database")
          return user

1.1.4.) Četrti API se uporablja za namen brisanja uporabnikov iz zapisov podatkovne baze. Če se logiramo v spletno stran user_stats.html, kliknemo "USER DELETE". Tukaj vnesemo uporabniško ime, katerega hočemo izbrisati. Vneseti moramo svoje administratorsko geslo. (POGOJ ZA ADMINISTRATORJA   je obstoječ račun uporabnika - ISTA OSEBA). Z pomočjo "html form" vnesemo tri podatke (String), API preveri če se vsi podatki ujemajo v podatkovnih bazah in izvede izbris uporabnika. 

    ######  GET  ######
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


1.1.5.) Peti API se pa uporablja za posodabljanje uporabniškega računa. Če hoče uporabnik posodobiti svoje geslo in email, mora predtem vnesti             svoje uporabniško ime in geslo. Če se po poizvedbi podatki ujemajo, se podatki posodobijo v podatkovni bazi.


        ######  GET  ######
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
        
        
1.1.6.) Šesti API se uporablja za resetiranje uporabniškega dosežka (Ang. Score). Da se ohranja seja oziroma kateri uporabnik je trenutno prijavljen, se v "index.html" uporabi funkcija "WriteCookie". Ta funkcija shrani piškotek v localStorage in ga prenese na stran user_stats.html, kjer se ob pritisku "RESET MY SCORE " ta obstoječ podatek pošlje dinamično poleg URL-ja do spodnje navedenega API-ja. URL zahtevek http://93.103.156.225/reset_score/Jaka bi resetiral rezultat uporabnika "Jaka".


    ######  GET  ######
    @app.get("/score_reset/{input}")   # SCORE RESET 
    async def score_reset(input:str):
        username = input
        highest_score = 0
        played_matches= 0
        delete_user("database", "player_scores", username)
        update_user_score("database", username, highest_score, played_matches)  
        return {"STATUS": "Score reseted successfully"}


1.1.7.) Sedmi API se uproablja za izpis vseh zapisov za trenutno prijavljen uporabnik. V tekstni obliki podamo "user" in 
        nam API vrne vse podatke za specificiranega uporabnika v JSON obliki.
        
    ######  GET  ######
    @app.get("/all_users/{input}")
    async def load_user_stats(input:str):
        user = input
        user = show_user_data("database","users",str(user))
        return user

1.1.8.) Osmi API se uporablja za izpis najboljših igralcev in njihovih doseženih rezultatov. Izpiše torej od najboljšega dalje.

    ######  GET  ######
    @app.get("/scores")
    async def show_scores():
        scores = show_all_scores_chart("database","player_scores")
        return scores

1.1.X.) Poleg navedenih API-jev imamo v "server.py" še PUT, POST in DELETE. Ti sprejemajo JSON objekte in delujejo na Postman.
        Integracija HTML-ja z preostalimi API klici je povzročalo precejšne težave in nestabilnost aplikacije.

1.2.) back_end - database_main.py
        V tej datoteki imamo vse pripadajoče funkcija za opravljanje/branje/pisanje v naše podatkovne baze. Imamo 2 podatkovni bazi in sicer
        "database.db" in "exec_database.db". Prva je uporabljena za uporabniške račune medtem ko druga pa za administratorske podatke.

            ### database.db ###
            #users(
            #username TEXT PRIMARY KEY,
            #password TEXT,
            #email TEXT,
            #highest_score INT,
            #played_matches INT)
            
            #played_rounds(
            #match_ID text, 
            #username text,
            #time_played INT,
            #score INT,
            #current_time INT,
            #FOREIGN KEY(username) REFERENCES users(username))
            
            #player_scores (
            #username text,
            ##highest_score INT,
            #time_played INT )

            ### exec_database.db ###
            #admins(
            #username TEXT PRIMARY KEY,
            #password TEXT


2.)Za prednji del smo uporabili programski jezik HTML. Z pomočjo dveh datotek "index.html" in "user_stats.html" vršimo API klice (glej              podpoglavja 1.1.1. - 1.1.X.). Spletna stran mobilne aplikacije je dostopna na spletnem naslovu http://93.103.156.225


3.)Mobilna aplikacija je zasnovana v razvojnem okolju Android Studio in jo poganja Java. Mobilna aplikacija deluje, vendar je pri                    integraciji mobilne aplikacije (Java kode) z API klici prišlo do določenih nevšečnosti. Težave so bile pri vzpostavljanju URL povezave med aktivnim Java programom in fizičnih spletnim strežnikom, ki predstavlja zaledni del. 


4.) Navodila za testiranje
4.0.)http://93.103.156.225
4.1.)Username = Tilen  &  Password = geslo
4.2.)Dvojni klik na "OK"
4.3.)Klik "SHOW_SCORES" nam prikaže dosežene rezultate (klik <--)
4.4.)Klik "USER_STATS" nam prikaže naše parametre in pdoobno (klik <--)
4.5.)Klik "UPDATE_STATS" nam nudi možnost posodobitve, tukaj lahko spremenimo naprimer email in rpeverimo njegovo delovanje
4.6.)Klik "RESET_MY_SCORE" nam ponastavi dosežen rezultat (klik <--)
4.7.)Klik "DELETE_USER" omogoča administratorju izbris uporabnika  [ADMIN = admin, PASSWORD = admin_password, Delete user = Jaka] (klik <--) 
4.8.)Klik "LOG_OUT" se uproabnik izpiše iz svojega računa in ga vrne nazaj na mesto prijave 


5.) input --> API --> output

http://93.103.156.225 --->   @app.get("/")   # MAIN  index  PAGE     
                             async def root():
                                 return FileResponse('index.html', media_type='text/html')    ---> index.html

------------------------------------------------------------------------------------------------------------------
    
http://93.103.156.255                            
/login?user=Tilen&password=geslo  --->    @app.get("/login")   # LOGIN          
                                          async def login(user: str = "",
                                                          password: str = ""):
                                                if(username_lookup("database", "users", str(user))):
                                                    if(user == specific_data_lookup("database", "users","username",1,user)  and  password ==                                                                 specific_data_lookup("database","users","username",2,user)):      
                                                           return FileResponse('game_stats.html', media_type='text/html')
                                                     else:
                                                           return FileResponse('index.html', media_type='text/html')
                                                 else:
        user_stats.html            <---                return FileResponse('index.html', media_type='text/html')

---------------------------------------------------------------------------------------------------------------------

http://93.103.156.255                            
/show_all_usernames                  --->        @app.get("/show_all_usernames")  # FETCH PLAYER NAMES
                                                 async def load_stats():
                                                     user = show_all_usernames("database")
                                

    '{                             <--            return user
    "Jaka",
    "Tilen",
    "Primož"
     }'
---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/
delete?admin=admin&
password=admin_pass&
delete_u=Anze        --->    @app.get("/delete")   # ADMIN LOGIN and USER DELETE
                                  async def admin_login(admin: str = "",
                                                          password: str = "",
                                                          delete_u: str = ""):
                                        if(username_lookup("exec_database", "admins", str(admin))):
                                            #return {"STATUS": "1"}
                                            if(admin == specific_data_lookup("exec_database", "admins","username",1,admin)  and  password ==                                                      specific_data_lookup("exec_database","admins","username",2,admin)): 
                                                delete_user("database", "users", delete_u)
                                                return {"STATUS": "User deleted successfully"}
                                            else:
                                                return {"STATUS": "User or credentials iINCORRECT"}
                                        else:
  removes user "Anze"     <---              return {"STATUS": "credentials iINCORRECT"}
    
---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/
user_update?new_email=NEW_EMAIL&
new_password=NEW_PASSWORD&
curr_user=username&
curr_password=password            --->            @app.get("/user_update")   # UPDATE USER SETTINGS 
                                                  async def user_update(new_email: str = "",
                                                                        new_password: str="",
                                                                        curr_user: str = "",
                                                                        curr_password: str = ""):
                                                    if(username_lookup("database", "users", str(curr_user))):
                                                        if(curr_user == specific_data_lookup("database", "users","username",1,curr_user)  and                                                                 curr_password == specific_data_lookup("database","users","username",2,curr_user)):
                                                            update_user_stats("database", curr_user, new_password, new_email)
                                                            return {"STATUS": "User settings updated successfully"}
                                                        else:
                                                            return {"STATUS": "Username and password INCORRECT"}
                                                    else:
user "username" updates                 <---            return {"STATUS": "Username INCORRECT"}
his/her current password & email                 
with NEW_PASSWORD nad NEW_EMAIL

---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/
score_reset/Tilen                    --->            @app.get("/score_reset/{input}")   # SCORE RESET 
                                                     @app.get("/score_reset/{input}")   # SCORE RESET 
                                                     async def score_reset(input:str):
                                                         username = input
                                                         highest_score = 0
                                                         played_matches= 0
                                                         delete_user("database", "player_scores", username)
                                                         update_user_score("database", username, highest_score, played_matches)  
 Logged user score resets to 0       <---                return {"STATUS": "Score reseted successfully"}

---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/
all_users/Tilen                      --->            @app.get("/all_users/{input}")
                                                     async def load_user_stats(input:str):
                                                        user = input
                                                        user = show_user_data("database","users",str(user))
'{                                   <---               return users
1,
"Tilen",
"geslo",
"somebodyfells@gmail.com",
0,
0
}'  
JSON OBJECT
Za indeksiranje glej podpoglavje 1.2.

---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/
played_rounds/username                --->            @app.get("/played_rounds/{input}")
                                                      async def load_user_stats(input:str):
                                                         user = input
                                                         user_matches = show_user_matches("database","played_rounds",str(user))
'{                                   <---                return users_matches
"match",
"Tilen",
33,
12,
current_time
}'
JSON OBJECT

---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/
scores                                --->            @app.get("/scores")
                                                      async def show_scores():
                                                         scores = show_all_scores_chart("database","player_scores")
'{                                   <---                return scores
      {
      30,
   "Primož",
      4
      },
      
      {
      20,
    "Jaka",
      8
      },
      .
      .
      .
}'                                                       
JSON OBJECTS INSIDE JSON (za indeksiranje glej podpoglavje 1.2.)

---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/             --->             ######  POST  #######
add_user                                                class Item_1(BaseModel):
method: POST                                                username:       str
input: JSON                                                 password:       str
'{                                                          email:          str
"username":"Tilen",                                         highest_score:  int
"password":"geslo",                                         played_matches: int
"email":"something@gmail.com",                           @app.post("/add_user")
"highest_score":0,                                       def handle_json_1(item: Item_1):                
"played_matches":0                                          item = jsonable_encoder(item)
}'                                                          username =       item["username"]
                                                            password =       item["password"]
                                                            email =          item["email"]
                                                            highest_score =  item["highest_score"]
                                                            played_matches = item["played_matches"]
                                                            add_data("database","users", username , password, email, highest_score,                                                                                    played_matches)
new user data received                 <---                 return {"STATUS": "user data received"}

---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/                     --->         ######  POST  #######
add_match                                               class Item_2(BaseModel):
method:POST                                                 username:       str
input:JSON                                                  highest_score:  int
'{                                                          time_played:    int
"username":"Tilen",                                     @app.post("/add_match")
"highest_score":30,                                     def handle_json_2(item: Item_2):
"time_played": 13                                           item = jsonable_encoder(item)
 }'                                                         username =      item["username"]
                                                            highest_score = item["highest_score"]
                                                            time_played =   item["time_played"]
                                                            add_player_score("database","player_scores", username, highest_score, time_played)
 new match data received                   <---             return {"STATUS": "match data received"}

---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/                                 ######  PUT  #######
/update_user/Tilen                                     class Item_3(BaseModel):
method:PUT                                                  password:       str
input:JSON                                                  email:          str
'{                                                          highest_score:  int
"password":"geslo",                                         played_matches: int
"email":"someone@gmail.com,                             @app.put("/update_user/{input}")
"highest_score": 24                                     def handle_json_3(item: Item_3, input: str =""):
"played_matches": 13                                        item = jsonable_encoder(item)
}'                                                          username =      input
                                                            password  =     item["password"]
                                                            email =         item["email"]
                                                            highest_score = item["highest_score"]
                                                            played_matches =   item["played_matches"]
                                                            update_user("database", username, password ,email ,highest_score ,played_matches)
 updates existing "Tilen" stats          <---               return {"STATUS": "user update received"}
                                                            
---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/Tilen                            ######  PUT  #######
                                                       class Item_3(BaseModel):
method: PUT                                                  score:  int
input: JSON                                             @app.put("{input}")     
'{                                                      def handle_json_3(item: Item_3, input: str =""):
"score": 99                                                  item = jsonable_encoder(item)
}'                                                           user =  input                                                             
                                                             score = item["score"]
                                                             played_matches = 0
                                                             update_user_score("database", user, score, played_matches)  
                                                             return {"STATUS": "user score updated"}

---------------------------------------------------------------------------------------------------------------------
http://93.103.156.225/
Primož                                 --->                 ######  DELETE  #######
method: DELETE                                              @app.delete("/{input}")
                                                            async def delete_record(input: str = ""):
                                                                user = input
                                                                delete_user("database", "users", str(user))
deletes username "Primož"             <---                      return{"STATUS": "user removed from users"}

---------------------------------------------------------------------------------------------------------------------


