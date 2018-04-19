// Get access to the class
// Add script in package.json file and use command `npm run dev-test`

const Wallet = require('./wallet');
const wallet = new Wallet();
console.log(wallet.toString());