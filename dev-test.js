// Get access to the class
// Add script in package.json file and use command `npm run dev-test`

const Blockchain = require('./blockchain');

const bc = new Blockchain();

for (let i = 0; i < 10; i++) {
    console.log(bc.addBlock(`foo ${i}`).toString());
}
