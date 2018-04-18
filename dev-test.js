// explore the block class

// Get access to the class
// Add script in package.json file and use command `npm run dev-test`
const Block = require('./block');

const fooBlock = Block.mineBlock(Block.genesis(), 'foo');

console.log(fooBlock.toString());
