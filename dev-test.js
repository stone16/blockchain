// explore the block class

// Get access to the class
// Add script in package.json file and use command `npm run dev-test`
const Block = require('./block');

const block = new Block('123789325','lastHash','hash','foo data');

console.log(block.toString());
console.log(Block.genesis().toString());

