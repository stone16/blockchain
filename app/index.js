const express = require('express'); 
const bodyParser = require('body-parser');
const Blockchain = require('../blockchain');

// define the port we want to listen to
// process.env.HTTP_PORT means user can choose their preferred port on command line
const HTTP_PORT = process.env.HTTP_PORT || 3001;

// create an express application
const app = express();
const bc = new Blockchain();

// allow us to receive json with post request 
app.use(bodyParser.json());

// return the blocks of current blockchain
app.get('/blocks',(req, res) => {
    res.json(bc.chain);
});

app.post('/mine', (req, res) => {
    // the body parser help us the decode the body
    const block = bc.addBlock(req.body.data);
    // see the new block
    console.log(`New block added: ${block.toString()}`);
    // respond back the updated chain of blocks that includes the new added one
    res.redirect('/blocks');
});

// run the application 
app.listen(HTTP_PORT, () => {
    console.log(`Listening on port ${HTTP_PORT}`);
});
