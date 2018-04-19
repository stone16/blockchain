const Block = require('./block');
const { DIFFICULTY } = require('../config');
// describe function  
// 1st para: description of the test
// 2nd para: callback arrow function, contains a set of tests

describe('Block', () => {
    let data;
    let lastBlock;
    let block;

    beforeEach(() => {
        data = 'foo-data';
        lastBlock = Block.genesis();
        block = Block.mineBlock(lastBlock, data);
    });

    // data is a special variable, thus surrounded by ``
    it('sets the `data` to match the input', () => {
        expect(block.data).toEqual(data);
    });

    it('sets the `lastHash` to match the hash of the last block', () => {
        expect(block.lastHash).toEqual(lastBlock.hash);
    });

    it('generate a hash that matches the difficulty', () => {
        expect(block.hash.substring(0, block.difficulty)).toEqual('0'.repeat(block.difficulty));
        console.log(block.toString());
    });

    it('lowers the difficulty for slowly mined blocks', () => {
        expect(Block.adjustDifficulty(block, block.timestamp + 23123324)).toEqual(block.difficulty - 1);
    });

    it('raised the difficulty for quickly mined blocks', () => {
        expect(Block.adjustDifficulty(block, block.timestamp + 1)).toEqual(block.difficulty + 1);
    });
});