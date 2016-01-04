function handleError(response) {
    swal({
        title: "Error...",
        text: response.responseJSON.message,
        type: "error",
        timer: 2000});
}

var flights = {};

function handleSearchTicket(data){
    // console.log(data);
    var $tHTML = "";
    $.each(data, function(i, flight) {
        flights[flight.id] = flight;
        var $tr = "<tr>" +
                "<td>" + flight.flightNumber + "</td>" +
                "<td>" + flight.startTime + "</td>" +
                "<td>" + flight.arrivalTime + "</td>" +
                "<td>" + flight.firstCount + "</td>" +
                "<td>" + flight.firstPrice + "</td>" +
                "<td>" + flight.economyCount + "</td>" +
                "<td>" + flight.economyPrice + "</td>";
        if (flight.firstCount > 0) {
            $tr += '<td><input type="checkbox" id="isFirst-' + flight.id + '"></td>';

        }
        else {
            $tr += '<td><input type="checkbox" id="isFirst-' + flight.id + '"></td>';
        }
        if (flight.firstCount > 0 || flight.economyCount > 0) {
            $tr += '<td><button type="button" class="btn btn-primary" data-id="' + flight.id + '">预订</button></td></tr>';
        }
        else {
            $tr += '<td><button type="button" class="btn btn-primary" disabled="disabled">预订</button></td></tr>';
        }
        console.log($tr);
        $tHTML += $tr;
    });
    $("#searchRet").find('tbody').html($tHTML);
}

$("#search").click(
    function() {
        $("form").ajaxSubmit({"success": handleSearchTicket, "error": handleError});
        return false;
    }
);

function doOrderTicket(e) {
    var button = e.target;
    if (button.tagName !== "BUTTON") {
        return;
    }
    console.log("flight ID is: " + button.dataset.id);
    var flightId = button.dataset.id;
    var orderFlight = flights[flightId];
    console.log(orderFlight);
    var isFirst = $("#isFirst-" + flightId)[0].checked;
    console.log("is first: " + isFirst);
    swal({
    	title: "请输入乘客姓名",
    	text: "'&'分隔多位乘客",
    	type: "input",
    	showCancelButton: true,
    	closeOnConfirm: false
    }, function(passengers) {
        console.log(passengers);
    	if (passengers === "") {
    		swal.showInputError("至少输入一名乘客!");
            return false;
    	}
    	$.ajax({
    		url: "/ticket/order",
            data: { passengers: passengers,
                    flightId: flightId,
                    isFirst: isFirst},
            type: "POST"
    	})
    	.done(function(data) {
    	    console.log(data);
    	    if(data.error!=0){
                swal({
                title: "Error...",
                text: data.message,
                type: "error",
                timer: 2000});
            }
            else{
                swal({
                title:"预订成功!",
                text:" 你已成功预订机票，请查看历史订单!",
                type:"success",
                timer:2000});
            }
    	})
    	.error(function(data) {
    	    console.log(data);
            swal.showInputError("预订失败，内部错误");
    	});
    });
}

$("#searchRet").click(doOrderTicket);

function handleSearchOrder(data){
    // console.log(data);
    var $tHTML = "";
    $.each(data, function(i, order) {
        // TODO build table
        var $tr = "<tr>" +
                "<td>" + order.flightNumber + "</td>" +
                "<td>" + order.startTime + "</td>" +
                "<td>" + order.arrivalTime + "</td>" +
                "<td>" + order.firstCount + "</td>" +
                "<td>" + order.firstPrice + "</td>" +
                "<td>" + order.economyCount + "</td>" +
                "<td>" + order.economyPrice + "</td>";
        if (order.firstCount > 0) {
            $tr += '<td><input type="checkbox" id="isFirst-' + flight.id + '"></td>';

        }
        else {
            $tr += '<td><input type="checkbox" id="isFirst-' + flight.id + '"></td>';
        }
        if (order.firstCount > 0 || order.economyCount > 0) {
            $tr += '<td><button type="button" class="btn btn-primary" data-id="' + flight.id + '">预订</button></td></tr>';
        }
        else {
            $tr += '<td><button type="button" class="btn btn-primary" disabled="disabled">预订</button></td></tr>';
        }
        console.log($tr);
        $tHTML += $tr;
    });
    $("#searchHistoryRet").find('tbody').html($tHTML);
}

$("#searchHistory").click(
    function() {
        $("form").ajaxSubmit({"success": handleSearchOrder, "error": handleError});
        return false;
    }
);

