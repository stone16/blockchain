package com.myblockchain.model;

import com.google.common.collect.Lists;
import com.myblockchain.utils.BlockChainUtils;
import lombok.Data;

import java.util.List;

@Data
public class MerkleTree {

    // Root Node
    private Node root;

    // Leaf Node's hash
    private String[] leafHashes;

    public MerkleTree(String[] leafHashes) {
        constructTree(leafHashes);
    }

    /**
     * Construct Merkle Tree froom bottom
     *
     * @param leafHashes
     */
    private void constructTree(String[] leafHashes) {
        if (leafHashes == null || leafHashes.length < 1) {
            throw new RuntimeException("ERROR:Fail to construct merkle tree ! leafHashes data invalid ! ");
        }
        this.leafHashes = leafHashes;
        List<Node> parents = bottomLevel(leafHashes);
        while (parents.size() > 1) {
            parents = internalLevel(parents);
        }
        root = parents.get(0);
    }

    /**
     * Construct a internal level
     *
     * @param children
     * @return
     */
    private List<Node> internalLevel(List<Node> children) {
        List<Node> parents = Lists.newArrayListWithCapacity(children.size() / 2);
        for (int i = 0; i < children.size() - 1; i += 2) {
            Node child1 = children.get(i);
            Node child2 = children.get(i + 1);

            Node parent = constructInternalNode(child1, child2);
            parents.add(parent);
        }

        if (children.size() % 2 != 0) {
            Node child = children.get(children.size() - 1);
            Node parent = constructInternalNode(child, null);
            parents.add(parent);
        }

        return parents;
    }

    /**
     * Construct bottom level
     *
     * @param hashes
     * @return
     */
    private List<Node> bottomLevel(String[] hashes) {
        List<Node> parents = Lists.newArrayListWithCapacity(hashes.length / 2);

        for (int i = 0; i < hashes.length - 1; i += 2) {
            Node leaf1 = constructLeafNode(hashes[i]);
            Node leaf2 = constructLeafNode(hashes[i + 1]);

            Node parent = constructInternalNode(leaf1, leaf2);
            parents.add(parent);
        }

        if (hashes.length % 2 != 0) {
            Node leaf = constructLeafNode(hashes[hashes.length - 1]);
            // 奇数个节点的情况，复制最后一个节点
            Node parent = constructInternalNode(leaf, leaf);
            parents.add(parent);
        }

        return parents;
    }

    /**
     * Construct leaf node
     *
     * @param hash
     * @return
     */
    private static Node constructLeafNode(String hash) {
        Node leaf = new Node();
        leaf.hash = hash;
        return leaf;
    }

    /**
     * Construct internal node
     *
     * @param leftChild
     * @param rightChild
     * @return
     */
    private Node constructInternalNode(Node leftChild, Node rightChild) {
        Node parent = new Node();
        if (rightChild == null) {
            parent.hash = leftChild.hash;
        } else {
            parent.hash = internalHash(leftChild.hash, rightChild.hash);
        }
        parent.left = leftChild;
        parent.right = rightChild;
        return parent;
    }

    /**
     * Calculate internal node hash
     *
     * @param leftChildHash
     * @param rightChildHash
     * @return
     */
    private String internalHash(String leftChildHash, String rightChildHash) {
        String mergedHash = leftChildHash.concat(rightChildHash);
        return BlockChainUtils.getSHA256Hash(mergedHash);
    }

    /**
     * Define Merkle Tree Node
     */
    @Data
    public static class Node {
        private String hash;
        private Node left;
        private Node right;
    }

}
