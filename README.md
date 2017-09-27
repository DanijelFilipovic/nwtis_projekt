# Projekt iz kolegija "Napredne Web tehnologije i servisi"
Projekt je amalgam zadaća koje su dobivene na laboratorijskim vježbama iz kolegija "Napredne Web tehnologije i servisi." Sastoji se od tri Web aplikacija, a cijeli projekt orijentiran je na registriranje novih lokacija na temelju adresa i, u određenom vremenskom intervalu, spremati njihove meteorološke podatke.

### Prva aplikacija
Prva aplikacija se vrti na Tomcat poslužitelju, a vidiljivi dio je rađen u JSP-u. Sastoji se od pet dijela:
 + *Socket* poslužitelj u kojem registrirani korisnici mogu tražiti dodavanje nove lokacije i admnisitratori mogu upravljati radom pozadinske dretve koja, u jednakim vremenskim intervalima, preuzima meteorološke podatke za spremljene adrese,
 + SOAP Web servisa,
 + RESTful Web servisa,
 + Web stranice za administratora koji omogućuje pregled korisnika, pregled dnevnika rada i pregled zahtjeva za *Socket* poslužitelj,
 + Web stranice za korisnike koji omogućuje pregled zahtjeva orema *Socket* poslužitelju koje je korisnik napravio.

### Druga aplikacija
Druga aplikacija se vrti na Glassfish poslužitelju i podijeljena je u EJB i Web module. Web dio je pisan u JSF-u. Sastoji se od tri dijela:
 + pozadinske dretve koja čita pristigle e-mail poruke iz Apache James-a i šalje JSM poruke koje čita treća aplikacija,
 + Web stranice za korisnika i administratora koje uglavnom komuniciraju s servisima i poslužiteljem iz prve aplikacije,
 + RESTful Web servisa koji pruža popis trenutno aktivnih korisnika.

### Treća aplikacija
Treća aplikacija se vrti na Glassfish poslužitelji i podijeljena je u EJB i Web module. Web dio je pisan u JSF-u. Omogućuje preuzimanje popisa aktivnih korisnika iz RESTful Web servisa iz druge aplikacije, ispis adresa koje su dodali te ispis trenutnih meteoroloških podataka za odabranu adresu.
