const Block = require('./block');

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
});