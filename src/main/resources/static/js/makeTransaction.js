/**
 * Created by LL.Chen on 4/22/18.
 */
var IP;
function showOptions(s) {
    IP = s[s.selectedIndex];
    console.log("IP: ", IP);
}

$(document).ready(function(){
    $('#data-table').submit(function(event){

        event.preventDefault();

        var amount = $('#amount').val();
        console.log(amount);
        var url = 'http://'+IP.value + ':8080/public-key';
        console.log(url);
        $.ajax({
            type:"GET",
            // contentType:"application/json",
            url:url,
            cache:false,
            timeout:50000,
            success: function (data) {
                console.log(data);
                var result = {};
                result.recipient = data;
                result.amount = amount;
                // var formatData = "\"recipient\":"+ data;
                // var formatAmount = ",\"amount\":"+ amount;
                // $.extend(result, data, amount);
                console.log("result: ", result);
                var resultString = JSON.stringify(result);
                console.log("resultString: ", resultString);
                $.ajax({
                    type:"POST",
                    url:"http://localhost:8080/transact",
                    contentType:"application/json",
                    data:resultString,
                    dataType:'text',
                    cache:false,
                    timeout:50000,
                    success: function(){
                        alert("Start to do the transaction");
                        console.log("get to transact");
                    },
                    error: function(e) {
                        alert("Not enough balance, make more money!");
                        console.log("error: ", e);
                        console.log(url);
                    }
                })
            },error:function(e){
                console.log("error: ", e);
                console.log(url);
            }
        });


    })


})