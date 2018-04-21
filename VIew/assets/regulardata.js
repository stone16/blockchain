// var columnName = {
// 	Age: '年龄',
// 	Season: '赛季'
// }

// ,text(function(column)){
// 	return columnName[column];
// }

function changeDataShown(){
  var e = document.getElementById("selectRegular");
  var value = e.options[e.selectedIndex].value;
  switch (value) {
    case "perGame":
        var path = '/data/pergame.json';
      break;
    case "totals":
        var path = '/data/totals.json';
      break;
    case "per36Minutes":
        var path = '/data/per36Minutes.json';
    break;
    case "per100Poss":
        var path = '/data/per100Poss.json';
    break;
    default:
       var path = '/data/per100Poss.json'
      break;
  }
  var path = '/data/blockDifficulty.json';
  d3.json(path, function (error,data) {
  	console.log(data);
  function tabulate(data, columns) {
            var table = d3.select('.row').append('table').attr('id','tabdata').attr('class','table');
            var thead = table.append('thead');
            var tbody = table.append('tbody');

            // append the header row
            thead.append('tr')
              .selectAll('th')
              .data(columns).enter()
              .append('th')
                .text(function (column) { return column; });

            // create a row for each object in the data
            var rows = tbody.selectAll('tr')
              .data(data)
              .enter()
              .append('tr');

            // create a cell in each row for each column
            var cells = rows.selectAll('td')
              .data(function (row) {
                return columns.map(function (column) {
                  return {column: column, value: row[column]};
                });
              })
              .enter()
              .append('td')
                .text(function (d) { return d.value; });

        return table;
      }
      if($("#tabdata")){
        $("#tabdata").remove();
      }

      // render the table(s)
      //,'Lg','Pos','G','GS','MP','FG','FGA','FG%','3P','3PA','3P%','2P','2PA','2P%'，'eFG%','FT','FTA','FT%','ORB','DRB','TRB','AST','STL','BLK','TOV','PF','PTS'
    tabulate(data, ['Season', 'Age','Tm','Lg','Pos','G','GS','MP','FG','FGA','FG%','3P','3PA','3P%','2P','2PA','2P%','eFG%','FT','FTA','FT%','ORB','DRB','TRB','AST','STL','BLK','TOV','PF','PTS']); 

});


}





            