var stompClient = null;

function connect() {
    var socket = new SockJS('/cashbox-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/cashbox', function (cashboxItem) {
            processReceivedItem(cashboxItem);
        });

        stompClient.subscribe('/topic/messages', function (messageItem) {
            console.log("subscribe messages")
            processReceivedMessageItem(messageItem);
        });

    });
}

function processReceivedItem(item) {
    console.log('2' +JSON.parse(item.body));
    drawNewCashboxRow(JSON.parse(item.body));
    recountCashboxBalance();
    recountAccountBalance();
    recountAccountDebts();
}

function drawNewCashboxRow(item) {
    let newTableRow = document.createElement('tr');
    newTableRow.style.cursor = 'pointer';
    newTableRow.class = 'cashbox_row';
    newTableRow.innerHTML =   '<td>' + item.id + '</td>' +
                              '<td>' + item.date + '</td>' +
                              '<td>' + ((item.completed) ? 'Проведен' : 'Не проведен') + '</td>' +
                              '<td>' + item.transactionItemName + '</td>' +
                              '<td>' + item.ownerFullName + '</td>' +
                              '<td>' + item.accountNumber + '</td>' +
                              '<td>' + item.transactionType + '</td>' +
                              '<td>' + item.amount + '</td>' +
                              '<td>' +
                                  '<div class="btn-group" role="group" aria-label="Basic outlined button group">' +
                                      '<a href="/admin/cashbox/edit/' + item.id + '" class="btn btn-default btn-sm"><i class="fa fa-pencil" aria-hidden="true"></i></a>' +
                                      '<a href="/admin/cashbox/delete/' + item.id + '" class="btn btn-default btn-sm"><i class="fa fa-pencil" aria-hidden="true"></i></a>' +
                                  '</div>' +
                              '</td>';
   let row_children = newTableRow.children;
   for(let j = 0; j < row_children.length - 1; j++) {
      row_children[j].addEventListener('click', function(){
          window.location.href = '/admin/cashbox/' + cashbox.id;
      });
   }

    $("#cashboxTable tbody").append(newTableRow);
}



function recountCashboxBalance() {
    $.get("/admin/cashbox/get-cashbox-balance", function(data) {
        $("#cashboxBalance").text(data.toString() + ' грн.');
    });
}

function recountAccountBalance() {
    $.get("/admin/cashbox/get-account-balance", function(data) {
        $("#accountBalance").text(data.toString() + ' грн.');
    });
}

function recountAccountDebts() {
    $.get("/admin/cashbox/get-account-debts", function(data) {
        $("#accountDebts").text(data.toString() + ' грн.');
    });
}

function processReceivedMessageItem(item) {
    console.log('msg 2'+JSON.parse(item.body));
    drawNewMessageRow(JSON.parse(item.body));

}
function drawNewMessageRow(msg) {
    console.log("draw msg table with web socket")
    // let dateString = msg.date.toString();
    // let dateParts = dateString.split(",");
    // let year = parseInt(dateParts[0]);
    // let month = parseInt(dateParts[1]) - 1;
    // let day = parseInt(dateParts[2]);
    // let hours = parseInt(dateParts[3]);
    // let minutes = parseInt(dateParts[4]);
    // let date = new Date(year, month, day, hours, minutes);
    // let formattedDate = ("0" + date.getDate()).slice(-2) + "." + ("0" + (date.getMonth() + 1)).slice(-2) + "." + date.getFullYear() + " " + ("0" + date.getHours()).slice(-2) + ":" + ("0" + date.getMinutes()).slice(-2);
    console.log('msg success');

    let newTableRow = document.createElement('tr');
    newTableRow.style.cursor = 'pointer';
    newTableRow.class = 'invoice_row';
    var index = msg.text.indexOf("<br>");
    var text = msg.text.substring(3, index);
    console.log("text" + text);
    var newText = text.substring(0, 70);
    if (newText.length == 70) {
        newText += '...'
    }
    console.log("newText" + newText)
    newTableRow.innerHTML =
        '<td><input type="checkbox" name="" id="" value="' + msg.id + '"></td>' +
        '<td>' + msg.receiversName + '</td>' +
        '<td><strong>' + msg.subject + '</strong> - ' + newText + '</td>' +
        '<td><p><strong>Новое сообщение</strong></p></td>';
    let row_children = newTableRow.children;
    for (let j = 1; j < row_children.length - 1; j++) {
        row_children[j].addEventListener('click', function () {
            window.location.href = '/cabinet/messages/' + msg.id;
        });
    }
    console.log('msg drawed');
    $("#messageTable tbody").prepend(newTableRow);

}

//function disconnect() {
//    if (stompClient !== null) {
//        stompClient.disconnect();
//    }
//    setConnected(false);
//    console.log("Disconnected");
//}

$(function () {
    connect();
});