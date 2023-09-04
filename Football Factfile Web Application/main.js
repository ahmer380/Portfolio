function normalizeText(text) {return text.normalize('NFD').replace(/[\u0300-\u036f]/g, '');}
function getPlayerProfile(playerName,isFootballer) {
    let urlEnd;
    if(isFootballer) {urlEnd = "players?search="+playerName+"&league=1";} //league field also required for footballers
    else {urlEnd = "coachs?search="+playerName;}
    return {
        "url": "https://v3.football.api-sports.io/" + urlEnd,
        "method": "GET",
        "timeout": 0,
        "headers": {
            "x-rapidapi-key": "fa614e08f67591d9273731752813afea",
            "x-rapidapi-host": "v3.football.api-sports.io"
        }
    };
}
function getPlayerTrophies(playerID,isFootballer) {
    let urlEnd;
    if(isFootballer) {urlEnd = "player="+playerID;}
    else {urlEnd = "coach="+playerID;}
    return {
        "url": "https://v3.football.api-sports.io/trophies?" + urlEnd,
        "method": "GET",
        "timeout": 0,
        "headers": {
            "x-rapidapi-key": "fa614e08f67591d9273731752813afea",
            "x-rapidapi-host": "v3.football.api-sports.io"
        }
    };
}

function displayHowToUsePopupBox() {
    $('.popup-title').html("Welcome!");
    $('.popup-text').empty();
    $('.popup-text').append("<ul>");
    $('.popup-text ul').append("<li>Either click on the &quot;Footballers&quot; or &quot;Managers&quot; tab in order to open a list of names</li>");
    $('.popup-text ul').append("<li>Upon clicking on the name of a player, its personal and trophy details are to be displayed on the webpage</li>");
    $('.popup-text ul').append("<li>Click on the image of a trophy in order to receive further information regarding which specific seasons the player won it</li>");
    $('.popup-text ul').append("<li>Due to the limitations of the API being used to retrieve the data, only a certain amount of new players may be fetched" +
        " at a time: you will be notified when a player is unavailable for viewing</li>");
    $('.popup-text').append("</ul>");
    $('.popup-box').css("display", "flex"); //Makes popupBox visible
}

function getValidLocalEntries() {
    const validLocalEntries = new Set();
    if(localStorage.length === 0) {return validLocalEntries;}
    for(const playerName of Object.keys(localStorage)) { //Key = playerName
        const dateCreated = localStorage.getItem(playerName).split('\n')[0];
        if(dateCreated === new Date().toDateString() || dateCreated === new Date(new Date().getTime() - 86400000).toDateString()){
            validLocalEntries.add(playerName); //Checks if entry was made today or yesterday
        }
    }
    return validLocalEntries;
}

class Player {
    /**
     * Both footballers and managers are considered as "players"
     * Stores all the necessary details to be displayed on the webpage
     */
    constructor(firstname,surname,birthInfo,nationality,image,height,weight) {
        this.trophyCount = 0;
        this.mostRecentTrophyWon = null; //tuple of [trophyName, trophyYear]
        this.trophies = {}; //Key = trophy-name, value = list of years when won
        this.otherTrophies = {} //Key = (non-verified)trophy-name, value = list of years when won
        this.firstname = firstname;
        this.surname = surname;
        this.birthInfo = birthInfo;
        this.nationality = nationality;
        this.image = image;
        this.height = height !== null ? height.replace(/\s/g, "") : "not known"; //removes space
        this.weight = weight !== null ? weight.replace(/\s/g, "") : "not known";
    }

    logTrophy(trophyName,trophyYear) {
        this.trophyCount ++;
        if(trophyName === "Super Cup") {trophyName = "Spanish/Italian/German Super Cup";}
        if(this.mostRecentTrophyWon === null) {this.mostRecentTrophyWon = [trophyName,trophyYear];}
        if(!(normalizeText(trophyName) in PlayerDatabaseSingleton.VERIFIED_TROPHIES)) {
            trophyName in this.otherTrophies ? this.otherTrophies[trophyName].push(trophyYear) : this.otherTrophies[trophyName] = [trophyYear];
            trophyName = "Other";
        }
        trophyName in this.trophies ? this.trophies[trophyName].push(trophyYear) : this.trophies[trophyName] = [trophyYear];
    }

    display() {
        $(".name").html(this.surname.toUpperCase() + ", " + this.firstname);
        $("#player-image").attr("src",this.image);
        $(".place-of-birth").html("Place of birth: " + this.birthInfo.place + ", " + this.birthInfo.country);
        $(".date-of-birth").html("Date of birth: " + this.birthInfo.date);
        $(".nationality").html("Nationality: " + this.nationality);
        $(".height").html("Height: " + this.height);
        $(".weight").html("Weight: " +this.weight);
        $(".trophy-count").html("Trophies won: " + this.trophyCount);
        $(".most-recent-trophy").html("Last trophy: " + this.mostRecentTrophyWon);

        //Displaying the trophies
        $('.trophy-showcase-container').empty();
        const sortedTrophies = Object.keys(this.trophies).sort((a, b) => this.trophies[b].length - this.trophies[a].length);
        //Sorts trophies by times won
        for(const trophy of sortedTrophies) {
            const trophyNameAndCount = $('<div class="trophy-name-and-count">' + trophy + " (x" + this.trophies[trophy].length + ")" + '</div>');
            const trophyImage = $('<img>').attr({src: normalizeText(trophy) in PlayerDatabaseSingleton.VERIFIED_TROPHIES ? PlayerDatabaseSingleton.VERIFIED_TROPHIES[normalizeText(trophy)]:
                    "https://www.pngkey.com/png/detail/46-465745_trophy-copy-trophy-icon-png-silver.png"});
            $('.trophy-showcase-container').append(
                $('<div class="trophy"></div>').append(
                    trophyNameAndCount,$('<div class="trophy-image-container"></div>').
                    append(trophyImage)));
        }
    }

    fillTrophyPopupBox(trophyName) {
        $('.popup-title').html(trophyName);
        $('.popup-text').empty();
        if(trophyName === "Other") {
            const sortedOtherTrophies = Object.keys(this.otherTrophies).sort((a, b) => this.otherTrophies[b].length - this.otherTrophies[a].length);
            for(const trophy of sortedOtherTrophies) {$('.popup-text').append(trophy + ": " + this.otherTrophies[trophy].join(', ') + "<br>");}
        }
        else {
            $('.popup-text').html(this.trophies[trophyName].join(', '));
        }
    }
}
class PlayerDatabaseSingleton {
    /**
     * The location where all data surrounding all players are stored
     * Responsible for retrieving player data from API-Sports
     * Or transmitting driver data to webpage
     */

    static FOOTBALLERS = [
        "Lionel Messi",
        "Cristiano Ronaldo",
        "Neymar",
        "Kylian Mbappe",
        "Robert Lewandowski",
        "Kevin De Bruyne",
        "Karim Benzema",
        "Harry Kane",
        "Thomas Muller",
        "Mohamed Salah",
        "Luka Modric",
        "Joshua Kimmich",
        "Sergio Ramos",
        "Virgil van Dijk",
        "Sergio Aguero",
        "Toni Kroos",
        "Manuel Neuer",
        "Luis Suarez",
        "Zlatan Ibrahimovic",
        "Mesut Ozil"
    ]; //20 footballers
    static MANAGERS = [
        "Pep Guardiola",
        "Jurgen Klopp",
        "Jose Mourinho",
        "Zinedine Zidane",
        "Diego Simeone",
        "Carlo Ancelotti",
        "Thomas Tuchel",
        "Mauricio Pochettino",
        "Massimiliano Allegri",
        "Hansi Flick"
    ]; //10 managers
    //Key = trophyName, value = png image of trophy
    static VERIFIED_TROPHIES = {
        "FIFA World Cup": "https://w7.pngwing.com/pngs/267/474/png-transparent-gold-fifa-world-cup-trophy-2010-fifa-world-cup-south-africa-2014-fifa-world-cup-1998-fifa-world-cup-fifa-world-cup-trophy-soccer-trophy-fifa-world-cup-objects-football.png",
        "Olympics": "https://www.pngitem.com/pimgs/m/60-604793_gold-medal-png-olympic-gold-medal-clipart-transparent.png",
        "UEFA European Championship": "https://www.vhv.rs/dpng/d/535-5359820_uefa-euro-2020-trophy-hd-png-download.png",
        "UEFA Nations League": "https://e7.pngegg.com/pngimages/614/337/png-clipart-2018-19-uefa-nations-league-national-football-team-uefa-competitions-trophy-uefa-cup-trophy-national-football-team-uefa.png",
        "CONMEBOL Copa America": "https://upload.wikimedia.org/wikipedia/commons/a/a1/COPA_Am%C3%A9rica_Trophy.png",
        "CONMEBOL/UEFA Finalissima": "https://upload.wikimedia.org/wikipedia/en/2/20/Artemio_franchi_trophy.png",
        "UEFA Champions League": "https://www.seekpng.com/png/detail/922-9224938_champion-league-trophy.png",
        "UEFA Europa League": "https://w7.pngwing.com/pngs/660/499/png-transparent-gray-metal-trophy-uefa-champions-league-uefa-super-cup-europe-2017-18-uefa-europa-league-fa-cup-premier-league-uefa-champions-league-uefa-super-cup-europe-thumbnail.png",
        "UEFA Europa Conference League": "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Trofeo_UEFA_Europa_Conference_League.svg/1200px-Trofeo_UEFA_Europa_Conference_League.svg.png",
        "UEFA Super Cup": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcScDoFRSBF9J_A72dXpx3MyAFNllepnQvBYpcBmUIuC&s",
        "FIFA Club World Cup": "https://banner2.cleanpng.com/20180927/hpz/kisspng-fifa-club-world-cup-final-intercontinental-cup-fif-filefifa-club-coppa-del-mondo-svg-wikimedia-com-5bada134954057.9671985015381056526113.jpg",
        "Premier League": "https://www.seekpng.com/png/full/318-3187173_premier-league-trophy-png-premier-league-title-png.png",
        "FA Cup": "https://www.seekpng.com/png/detail/911-9116766_fa-cup-draw-fa-cup-trophy-png.png",
        "Community Shield": "https://e7.pngegg.com/pngimages/652/184/png-clipart-2017-fa-community-shield-2013-fa-community-shield-2016-fa-community-shield-manchester-city-f-c-2012-fa-community-shield-football-cup-metal-super-cup.png",
        "League Cup": "https://lfccarabao2022.liverpoolecho.co.uk/img/du_cup_icon.png", //Carabao cup
        "La Liga": "https://www.kindpng.com/picc/m/401-4018108_laliga-messi-ronaldo-savage-la-liga-trophy-png.png",
        "Copa del Rey": "https://e7.pngegg.com/pngimages/395/973/png-clipart-spain-sport-2017-18-copa-del-rey-world-cup-trophy-trophy.png",
        "Spanish/Italian/German Super Cup": "https://i.pinimg.com/564x/c5/20/7d/c5207de6c08482a6139b353dd34142b5.jpg", //Spanish/Italian/German super cup
        "Serie A": "https://i.pinimg.com/originals/d0/80/7a/d0807a8a8d51cb6efd2e93bf5d2c86a2.png",
        "Coppa Italia": "https://i.pinimg.com/originals/66/0c/9a/660c9a02f1d14f56c1efe06db5d0d782.png",
        "Bundesliga": "https://banner2.cleanpng.com/20180612/azb/kisspng-2-bundesliga-fc-bayern-munich-borussia-dortmund-u-juan-arango-5b1f65711c6340.5028932515287842411163.jpg",
        "DFB Pokal": "https://kochundbergfeld.de/wp-content/uploads/2018/09/02-1_08_KB_Trophaen_DFB-Pokal_VV6A0429.jpg",
        "Ligue 1": "https://www.vhv.rs/dpng/d/116-1166030_french-ligue-1-trophy-hd-png-download.png",
        "Coupe de France": "https://upload.wikimedia.org/wikipedia/commons/1/1f/Coupe_de_France_trophy.png",
        "Trophee des Champions": "https://i.pinimg.com/originals/ef/f6/d6/eff6d6b9ded984d4b1393ec6851f2d76.png"
    };

    constructor() {
        if(!PlayerDatabaseSingleton.instance) {
            PlayerDatabaseSingleton.instance = this;
        }
        return PlayerDatabaseSingleton.instance;
    }

    display_footballer_list() {
        //Blue colour scheme
        $(".name-list").empty();
        for(const footballer of PlayerDatabaseSingleton.FOOTBALLERS) {
            $(".name-list").append("<li>" + footballer + "</li>");
        }
        $('.name-list li:nth-child(odd)').css("background-color", "darkblue");
        $('.name-list li:nth-child(even)').css("background-color", "midnightblue");

        $('.name-list li').click(async (event) => {
            const lastWord = $(event.currentTarget).text().split(' ').pop();
            try {await getAndDisplayPlayer(normalizeText(lastWord).toLowerCase(), true);}
            catch (error) {console.log(error)}
        });
    }
    display_manager_list() {
        $(".name-list").empty();
        for(const manager of PlayerDatabaseSingleton.MANAGERS) {
            $(".name-list").append("<li>" + manager + "</li>");
        }
        $('.name-list li:nth-child(odd)').css("background-color", "crimson");
        $('.name-list li:nth-child(even)').css("background-color", "darkred");

        $('.name-list li').click(async (event) => {
            const lastWord = $(event.currentTarget).text().split(' ').pop();
            try {await getAndDisplayPlayer(normalizeText(lastWord).toLowerCase(), false);}
            catch (error) {console.log(error)}
        });
    }

    async fetchAPIPlayerProfile(playerName,isFootballer) {
        return new Promise((resolve) => {
            $.ajax(getPlayerProfile(playerName, isFootballer))
                .done((r) => {
                    if(r.errors.length !== 0) {resolve(r.errors);}
                    else if(isFootballer) {resolve(r.response[0].player);}
                    else {resolve(r.response[0]);}
                });
        });
    }

    async fetchAPIPlayerTrophies(playerID, isFootballer) {
        return new Promise((resolve) => {
            $.ajax(getPlayerTrophies(playerID, isFootballer))
                .done((r) => {
                    if(r.errors.length !== 0) {resolve(r.errors);}
                    else {resolve(r.response);}
                });
        });
    }

    /**
     * Read off the localStorage for the player, and convert it into a player object
     */
    getPlayerObject(playerName) {
        /**
         * PlayerData format
         * 0: date
         * 1: firstname
         * 2: surname
         * 3: birthInfo
         * 4: nationality
         * 5: imageURL
         * 6: height
         * 7: weight
         * 8: trophyInfo
         */
        const playerData = localStorage.getItem(playerName.toLowerCase()).split('\n');
        const birthInfo = {
            date: playerData[3].split(',')[0],
            place: playerData[3].split(',')[1],
            country: playerData[3].split(',')[2]
        }
        const player = new Player(playerData[1], playerData[2], birthInfo, playerData[4], playerData[5], playerData[6], playerData[7]);
        for(const trophy of playerData[8].split('|')) {
            player.logTrophy(trophy.split(',')[0],trophy.split(',')[1]);
        }
        return player;
    }

    setLocalStorageEntry(profileAPIResponse,trophyAPIResponse) {
        //Key = player name, value = player details
        let entryContent = "";
        entryContent += new Date().toDateString() + "\n"; //Line1: The day of writing the file
        entryContent += profileAPIResponse.firstname.split(' ')[0] + "\n"; //Line2: First name
        entryContent += profileAPIResponse.name.split(' ').pop(0) + "\n"; //Line3: Last name
        entryContent += profileAPIResponse.birth.date + ", " + profileAPIResponse.birth.place + ", " + profileAPIResponse.birth.country + "\n"; //Line4: birthdate, birthplace, country of birth
        entryContent += profileAPIResponse.nationality + "\n"; //Line5: Nationality
        entryContent += profileAPIResponse.photo + "\n"; //Line6: Photo url (as string)
        entryContent += profileAPIResponse.height + "\n"; //Line7: Height
        entryContent += profileAPIResponse.weight + "\n"; //Line8: Weight

        for(const trophy of trophyAPIResponse) {
            if(trophy.place === "Winner") {
                //Line9: trophyLeague,trophySeason|for every trophy won by player
                entryContent += trophy.league + "," + trophy.season.split(" ")[0] + "|";
            }
        }
        entryContent = entryContent.length > 0 ? entryContent.slice(0, -1) : entryContent;
        localStorage.setItem(normalizeText(profileAPIResponse.name.split(' ').pop()).toLowerCase(),entryContent);
    }
}

function initializeEventListeners() {
    //Changing the player list to show footballers/managers, also in their functions adds functionality to clicking names
    $(".footballers-label").click(() => database.display_footballer_list());
    $(".managers-label").click(() => database.display_manager_list());
    //Display tutorial text when the "How to use button is clicked"
    $(".how-to-use").click(() => displayHowToUsePopupBox());
    //Close trophyInfo when 'X' is clicked on
    $('.popup-exit-button').click(() => $('.popup-box').css("display", "none"));
}

async function getAndDisplayPlayer(playerName,isFootballer) {
    function handleErroneousAPICall(erroneousResponse) {
        $('.popup-title').html("Something went wrong...");
        $('.popup-text').empty();
        if("requests" === Object.keys(erroneousResponse)[0].toLowerCase()) {
            $('.popup-text').html("Player currently unavailable for viewing<br>Please try again later");
        }
        else {
            $('.popup-text').html("Player currently unavailable for viewing<br>Please try again in one minute");
        }
        $('.popup-box').css("display", "flex"); //Makes popupBox visible
    }

    if(!getValidLocalEntries().has(playerName)) {
        //Entry doesn't exist or is outdated, API Calls required
        localStorage.removeItem(playerName); //Removes entry in case it is outdated
        const playerProfileAPIResponse = await database.fetchAPIPlayerProfile(playerName, isFootballer);
        if(Object.keys(playerProfileAPIResponse).length === 1) { //Erroneous if length is one
            handleErroneousAPICall(playerProfileAPIResponse);
            return;
        }
        const playerTrophyAPIResponse = await database.fetchAPIPlayerTrophies(playerProfileAPIResponse.id, isFootballer);
        if(Object.keys(playerTrophyAPIResponse).length === 1) { //Erroneous if length is one
            handleErroneousAPICall(playerTrophyAPIResponse);
            return;
        }
        database.setLocalStorageEntry(playerProfileAPIResponse,playerTrophyAPIResponse);
    }
    const currentPlayer = database.getPlayerObject(playerName);
    currentPlayer.display();
    //Show trophyInfo when clicked on
    $('.trophy-image-container').click((event) => {
        currentPlayer.fillTrophyPopupBox($(event.currentTarget).parent().find('.trophy-name-and-count').text().split('(')[0].trim());
        $('.popup-box').css("display", "flex"); //Makes trophyPopupBox visible
    });
}

async function initialize() {
    database = new PlayerDatabaseSingleton(); //Only time in which initialized
    initializeEventListeners();
}

//These are the only global variables in my script
var database;
$(() => {initialize();});