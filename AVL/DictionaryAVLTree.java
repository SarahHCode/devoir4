class AVLNode {
    String word, meaning;
    int height;
    AVLNode left, right;

    AVLNode(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
        this.height = 1;
        this.left = this.right = null;
    }
}
public class DictionaryAVLTree {
    private AVLNode root;


    private int height(AVLNode N) {
        if (N == null) return 0;
        return N.height;
    }

    /**
     * Méthode qui permet d’ajouter de nouveaux mots et leurs significations au dictionnaire. L’arbre doit rester
     * équilibré après chaque insertion.
     * @param node
     * @param word
     * @param meaning
     * @return la racine
     */
    private AVLNode insert(AVLNode node, String word, String meaning) {
        AVLNode rightEmplacement;
        // Add new node
        if (root == null) {
            root = new AVLNode(word, meaning);
            return root;
        } else {
            AVLNode rightEmplacementsParent = searchNodeParent(root, word);
            // word < word du parent
            if (word.compareTo(rightEmplacementsParent.word) < 0) {
                rightEmplacementsParent.left = new AVLNode(word, meaning);
                rightEmplacement = rightEmplacementsParent.left;
            // word > word du parent
            } else {
                rightEmplacementsParent.right = new AVLNode(word, meaning);
                rightEmplacement = rightEmplacementsParent.right;
            }
        }

        updateNodeAndAncestorsHeight(rightEmplacement);

        //balance tree - check from node gradually to its ancestors who's unbalanced, and if found, choose two nodes
        //towards new node direction
        if (rightEmplacement != root) {
            AVLNode currentAncestorsChild = null;
            AVLNode currentAncestorsGrandchild = null;
            AVLNode currentAncestor = rightEmplacement;
            // while not reached root yet
            while (currentAncestor != root) {
                currentAncestor = searchNodeParent(root, currentAncestor.word);
                // Check if tree at currentAncestor node is unbalanced
                if (heightDifference(currentAncestor) > 1 || heightDifference(currentAncestor) < -1) {
                    // Choose two nodes towards new node direction
                    // Closest child node towards biggest height direction
                    currentAncestorsChild = heightDifference(currentAncestor) > 0 ?
                            currentAncestor.left : currentAncestor.right;
                    // Closest grandchild node towards biggest height direction
                    currentAncestorsGrandchild = heightDifference(currentAncestorsChild) >= 0 ?
                            currentAncestorsChild.left : currentAncestorsChild.right;
                    rotation(currentAncestor, currentAncestorsChild, currentAncestorsGrandchild);
                }
            }
        }
        return root;
    }

    /**
     * Méthode ajoutée qui fait les rotations.
     * @param currentAncestor
     * @param currentAncestorsChild
     * @param currentAncestorsGrandchild
     */
    private void rotation(AVLNode currentAncestor, AVLNode currentAncestorsChild, AVLNode currentAncestorsGrandchild) {
        // Rotation simple vers la droite
        if (currentAncestorsGrandchild == currentAncestorsChild.left && currentAncestorsChild == currentAncestor.left) {
            currentAncestor.left = currentAncestorsChild.right;
            currentAncestorsChild.right = currentAncestor;
        // Rotation simple vers la gauche
        } else if (currentAncestorsGrandchild == currentAncestorsChild.right && currentAncestorsChild == currentAncestor.right) {
            currentAncestor.right = currentAncestorsChild.left;
            currentAncestorsChild.left = currentAncestor;
        // Rotation double vers la droite
        } else if (currentAncestorsGrandchild == currentAncestorsChild.right && currentAncestorsChild == currentAncestor.left) {
            currentAncestorsChild.right = currentAncestorsGrandchild.left;
            currentAncestor.left = currentAncestorsGrandchild.right;
            currentAncestorsGrandchild.left = currentAncestorsChild;
            currentAncestorsGrandchild.right = currentAncestor;
        // Rotation double vers la gauche
        } else if (currentAncestorsGrandchild == currentAncestorsChild.left && currentAncestorsChild == currentAncestor.right) {
            currentAncestor.right = currentAncestorsGrandchild.left;
            currentAncestorsChild.left = currentAncestorsGrandchild.right;
            currentAncestorsGrandchild.left = currentAncestor;
            currentAncestorsGrandchild.right = currentAncestorsChild;
        }
        updateNodeAndAncestorsHeight(currentAncestor);
        updateNodeAndAncestorsHeight(currentAncestorsChild);
        updateNodeAndAncestorsHeight(currentAncestorsGrandchild);
    }

    /**
     * Méthode ajoutée qui retourne la différence de hauteur entre l'arbre gauche - l'arbre droit.
     * Si < 0: arbre gauche < arbre droit
     * Si > 0: arbre gauche > arbre droit
     * Si = 0: arbres gauche et droit de même hauteur
     * @param node
     * @return différence de hauteur entre le noeud gauche - le noeud droit
     */
    private int heightDifference(AVLNode node) {
        return (node.left == null ? 0 : node.left.height) - (node.right == null ? 0 : node.right.height);
    }

    public void insert(String word, String meaning) {
        root = insert(root, word, meaning);
    }

    /**
     * Méthode qui permet de retirer des mots du dictionnaire. L’arbre doit rester équilibré après chaque suppression.
     * @param root
     * @param word
     * @return
     */
    private AVLNode delete(AVLNode root, String word) {
        remove(root, word);

        return root;
    }

    // For checking disbalance after (if removed node had two children, check disbalance at its position)
    boolean hasTwoChildren = false;

    private AVLNode remove(AVLNode root, String word) {
        // Search for the node to be removed
        AVLNode current = searchNode(root, word);
        AVLNode parent = current != root ? searchNodeParent(root, word) : null;

        // If the node is not found
        if (current == null) {
            return root;
        }

        // Case 1: Node has no children (leaf node)
        if (current.left == null && current.right == null) {
            if (parent == null) {
                // If the node to be removed is the root
                root = null;
            } else if (current == parent.left) {
                parent.left = null;
            } else {
                parent.right = null;
            }
        }
        // Case 2: Node has one child
        else if (current.left == null || current.right == null) {
            AVLNode child = (current.left != null) ? current.left : current.right;
            if (parent == null) {
                // If the node to be removed is the root
                root = child;
            } else if (current == parent.left) {
                parent.left = child;
            } else {
                parent.right = child;
            }
        }
        // Case 3: Node has two children
        else {
            // Find the inorder predecessor (largest node in the left subtree)
            AVLNode predecessor = findPredecessor(current.left); //findSuccessor(current.right)
            // Replace the node's key with the predecessor's key
            current.word = predecessor.word;
            current.meaning = predecessor.meaning;
            hasTwoChildren = true;
            // Recursively remove the predecessor from the left subtree
            remove(root, predecessor.word);
        }

        updateNodeAndAncestorsHeight(current);

        //balance tree - check from node gradually to its ancestors who's unbalanced, and if found, choose two nodes
        //towards new node direction
        if (current != root) {
            AVLNode currentAncestorsChild = null;
            AVLNode currentAncestorsGrandchild = null;
            AVLNode currentAncestor = !hasTwoChildren ? current : (current.left != null ? current.left : current.right);
            // while not reached root yet
            while (currentAncestor != root) {
                currentAncestor = searchNodeParent(root, currentAncestor.word);
                // Check if tree at currentAncestor node is unbalanced
                if (heightDifference(currentAncestor) > 1 || heightDifference(currentAncestor) < -1) {
                    // Choose two nodes towards new node direction
                    // Closest child node towards biggest height direction
                    currentAncestorsChild = heightDifference(currentAncestor) > 0 ?
                            currentAncestor.left : currentAncestor.right;
                    // Closest grandchild node towards biggest height direction
                    currentAncestorsGrandchild = heightDifference(currentAncestorsChild) >= 0 ?
                            currentAncestorsChild.left : currentAncestorsChild.right;
                    rotation(currentAncestor, currentAncestorsChild, currentAncestorsGrandchild);
                }
            }
        }

        return root;
    }

    /**
     * Helper method to find the inorder predecessor (largest node in the left
     * subtree).
     *
     * @param node The root of the subtree to search for the predecessor.
     * @return The inorder predecessor node.
     */
    private AVLNode findPredecessor(AVLNode node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    private void updateNodeAndAncestorsHeight(AVLNode current) {
        // if node is not root, update its ancestors' height by moving up till the root
        if (current != root) {
            // while not reached root yet
            while (current != root) {
                current = searchNodeParent(root, current.word);
                current.height = Math.max(current.left == null ? 0 : current.left.height,
                        current.right == null ? 0 : current.right.height) + 1;
            }
        }
    }

    public void delete(String word) {
        root = delete(root, word);
    }

    /**
     * Méthode qui permet de trouver la signification d’un mot donné.
     * @param root
     * @param word
     * @return
     */
    public String search(AVLNode root, String word) {
        return searchNode(root, word) != null ? searchNode(root, word).meaning : "Not in dictionary";
    }

    public String search(String word) {
        return search(root, word);
    }

    /**
     * Méthode ajoutée qui permet de trouver le node correspondant à word (node null s'il n'existe pas encore).
     * @param root
     * @param word
     * @return node correspondant à word (node null s'il n'existe pas encore)
     */
    private AVLNode searchNode(AVLNode root, String word) {
        // Arbre vide
        if (root == null) return root;
        // Set currentNode for search
        AVLNode currentNode = root;
        while (!currentNode.word.equals(word)) {
            // Si word < word du noeud courant
            if (word.compareTo(currentNode.word) < 0) {
                // Déplacer à gauche
                currentNode = currentNode.left;
                if (currentNode == null) return null;
            // Si word > word du noeud courant
            } else if (word.compareTo(currentNode.word) > 0) {
                // Déplacer à droite
                currentNode = currentNode.right;
                if (currentNode == null) return null;
            }
        }
        return currentNode;
    }

    /**
     * Méthode ajoutée qui permet de trouver le parent du node correspondant à word.
     * @param root reçoit un root non null (root ne peut pas avoir un parent) qui n'est pas le node du word
     * @param word
     * @return parent du node correspondant à word
     */
    private AVLNode searchNodeParent(AVLNode root, String word) {
        AVLNode parentNode = root;
        // Set currentNode for search
        AVLNode currentNode = root;
        while (!currentNode.word.equals(word)) {
            // Si word < word du noeud courant
            if (word.compareTo(currentNode.word) < 0) {
                // Déplacer à gauche
                parentNode = currentNode;
                currentNode = currentNode.left;
                if (currentNode == null) break;
                // Si word > word du noeud courant
            } else if (word.compareTo(currentNode.word) > 0) {
                // Déplacer à droite
                parentNode = currentNode;
                currentNode = currentNode.right;
                if (currentNode == null) break;
            }
        }
        return parentNode;
    }

    private void inOrderTraversal(AVLNode node) {
        if (node != null) {
            inOrderTraversal(node.left);
            System.out.println("Word: " + node.word + ", Meaning: " + node.meaning + ", Left: " + (node.left != null ? node.left.word : "null") + ", Right: " + (node.right != null ? node.right.word : "null") + ", Height: " + node.height);
            inOrderTraversal(node.right);
        }
    }

    public void displayDictionary() {
        inOrderTraversal(root);
    }

    public static void main(String[] args) {
        DictionaryAVLTree dictionary = new DictionaryAVLTree();
    
        // Example insertions
        String[][] wordsAndMeanings = {
                {"Harmony", "The combination of simultaneously sounded musical notes to produce chords and chord progressions having a pleasing effect."},
                {"Ephemeral", "Lasting for a very short time."},
                {"Serendipity", "The occurrence and development of events by chance in a happy or beneficial way."},
                {"Quintessential", "Representing the most perfect or typical example of a quality or class."},
                {"Eloquence", "Fluent or persuasive speaking or writing."},
                {"Melancholy", "A feeling of pensive sadness, typically with no obvious cause."},
                {"Labyrinth", "A complicated irregular network of passages or paths in which it is difficult to find one's way; a maze."},
                {"Solitude", "The state or situation of being alone."}
            };
    
        // Insert each word and meaning into the dictionary
        for (String[] entry : wordsAndMeanings) {
            dictionary.insert(entry[0], entry[1]);
        }
    
        // Display the dictionary before deletion
        System.out.println("Dictionary content in alphabetical order before deletion:");
        dictionary.displayDictionary();
    
        // Deleting a word from the dictionary
        String wordToDelete = "Ephemeral";
        System.out.println("\nDeleting word: " + wordToDelete);
        dictionary.delete(wordToDelete);
    
        // Display the dictionary after deletion
        System.out.println("Dictionary content in alphabetical order after deletion:");
        dictionary.displayDictionary();
    
        // Searching for meanings
        String[] wordsToSearch = {"Harmony", "Eloquence", "Ephemeral"};
        System.out.println("\nSearching for words:");
        for (String word : wordsToSearch) {
            System.out.println("Meaning of '" + word + "': " + dictionary.search(word));
        }
    }
}

