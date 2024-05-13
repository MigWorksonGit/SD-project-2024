var stompClient = null;

function setConnected(connected) {
    if (connected) {
        document.getElementById("connect").setAttribute("disabled", '');
        document.getElementById("disconnect").removeAttribute("disabled");
        document.getElementById("conversation").style.display = 'block';
    }
    else {
        document.getElementById("conversation").style.display = 'hidden';
        document.getElementById("connect").removeAttribute("disabled");
        document.getElementById("disconnect").setAttribute("disabled", '');
    }
    document.getElementById("messages").innerHTML = "";
}
    
function connect() {
    var socket = new SockJS('/my-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            showMessage(JSON.parse(message.body).content);
        });            
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");        
}

function sendMessage() {
    stompClient.send("/app/message", {},
    JSON.stringify({'content': document.getElementById("message").value }));
}
    

function showMessage(message) {
    document.getElementById("messages").insertRow();
    document.getElementById("messages").append(message);
}
    

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); });
});

