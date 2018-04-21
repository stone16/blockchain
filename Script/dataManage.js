function addFunction1() {
     var name = document.getElementById("block").value;
     var value = '/data/' + name + '.json';
     console.log(value);

    value = 'data/blockDifficulty.json';
    console.log(value);
    d3.json(value, function (error, data) {
    	alert(error);
    	console.log('1213412');
        // use data here
        console.log(data);

    });

}

