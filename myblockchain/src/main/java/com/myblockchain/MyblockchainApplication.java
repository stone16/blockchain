package com.myblockchain;

import com.myblockchain.model.Block;
import com.myblockchain.model.Transaction;
import com.myblockchain.model.TransactionPool;
import com.myblockchain.services.blockchain.BlockChain;
import com.myblockchain.services.miner.Miner;
import com.myblockchain.services.network.P2pServer;
import com.myblockchain.services.wallet.Wallet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyblockchainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyblockchainApplication.class, args);
	}

	@Bean
	BlockChain blockChain() {
		return BlockChain.newBlockchain();
	}

	@Bean
	Wallet wallet() {
		return new Wallet();
	}

	@Bean
	TransactionPool transactionPool() {
		return new TransactionPool();
	}

	@Bean
	P2pServer p2pServer(BlockChain blockChain, TransactionPool transactionPool, Wallet wallet) {
		P2pServer p2pServer = new P2pServer(8888, blockChain, transactionPool, wallet);
		p2pServer.init();
		return p2pServer;
	}

	@Bean
	Miner Miner(BlockChain blockChain, TransactionPool transactionPool, Wallet wallet, P2pServer p2pServer) {
		return new Miner(blockChain, transactionPool, wallet, p2pServer);
	}
}

