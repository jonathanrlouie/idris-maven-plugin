module Main

data Tree a = Leaf
            | Node (Tree a) a (Tree a)

inorder : Tree a -> List a
inorder Leaf = []
inorder (Node left a right) = inorder left ++ [a] ++ inorder right

tree : Tree String
tree = Node
        (Node
          (Node Leaf "3" Leaf)
          "+"
          (Node Leaf "7" Leaf))
        "/"
        (Node Leaf "2" Leaf)

main : IO ()
main = printLn $ inorder tree
