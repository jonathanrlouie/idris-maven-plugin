module Main

data File : Type where [external]
data Document : Type where [external]
data Elements : Type where [external]
data Element : Type where [external]

%foreign "jvm:<init>(java/lang/String java/io/File),java/io/File"
prim_newFile : String -> PrimIO File

%foreign "jvm:parse(java/io/File java/lang/String org/jsoup/nodes/Document),org/jsoup/Jsoup"
prim_parse : File -> String -> PrimIO Document

%foreign "jvm:.select(org/jsoup/nodes/Document java/lang/String org/jsoup/select/Elements),org/jsoup/nodes/Document"
prim_select : Document -> String -> PrimIO Elements

%foreign "jvm:.first(org/jsoup/select/Elements org/jsoup/nodes/Element),org/jsoup/select/Elements"
prim_first : Elements -> PrimIO Element

%foreign "jvm:.text(org/jsoup/nodes/Element java/lang/String),org/jsoup/nodes/Element"
prim_text : Element -> PrimIO String

newFile : String -> IO File
newFile = primIO . prim_newFile

parse : File -> String -> IO Document
parse file charset = primIO $ prim_parse file charset

select : Document -> String -> IO Elements
select document query = primIO $ prim_select document query

first : Elements -> IO Element
first = primIO . prim_first

text : Element -> IO String
text = primIO . prim_text

main : IO ()
main = do
  file <- newFile "foo.html"
  document <- parse file "UTF-8"
  elements <- select document "h1"
  element <- first elements
  title <- text element
  putStrLn title
