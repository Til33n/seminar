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


1.1.5.) Peti API se pa uporablja za posodabljanje 

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




1.1.6.) Šesti API
1.1.7.) Sedmi API
1.1.8.) Osmi API
1.1.9.) Deveti API







1.2.) back_end - database_main.py



2.) front_end
