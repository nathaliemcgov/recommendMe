<?php

$file = "movies.csv";
$fileContents = file($file);
$trie = null;
session_start();

if(isset($_GET["media"])) {
    $media = $_GET["media"];
    if($media === "destroy") {
        session_destroy();
    }
} else {
    $media = "American Psycho";
}

if(isset($_SESSION["trie"])) {
    $trie = $_SESSION["trie"];
} else {
    $trie = buildTrie($trie, $fileContents);
    $_SESSION["trie"] = $trie;
}

$trie->getWordsFromPrefix($media);



function buildTrie($trie, $fileContents) {
    $trie = new KTrie();
    $i = 0;
    foreach ($fileContents as $value) {
        $i++;
        //if($i < 3000) {
        $trie->addWord($value);
        //}
    }
    return $trie;
}

class KTrie
{
    function __construct()
    {
        $this->root = new KTrieNode('');
    }

    function addWord($word)
    {
        $wordArray = str_split($word);
        $this->root->addSuffix($wordArray);
    }

    function hasWord($word)
    {
        $wordArray = str_split($word);
        return $this->root->hasSuffix($wordArray);
    }

    function printStructure()
    {
        $this->root->printStructure(0);
    }

    function printWords()
    {
        $this->root->printWords('');
    }

    function getWordsFromPrefix($prefix) {
        //navigate to node
        $node = $this->root;
        $lengthOfWord = strlen($prefix);
        for($i = 1; $i <= $lengthOfWord; $i++) {
            if($this->root->hasSuffix(str_split(substr($prefix, 0, $i)))) {
                $node = $node->getChild($prefix[$i - 1]);
             }
        }

        $node->printWords(substr($prefix, 0, $lengthOfWord - 1));
    }


}

class KTrieNode
{
    function __construct($s){ $this->value = $s; }

    function addSuffix($suffixArray)
    {
        if(!empty($suffixArray))
        {
            $firstChar = $suffixArray[0];
            $remnant = array_slice($suffixArray, 1);

            $childNode = $this->getChild($firstChar);
            if($childNode === FALSE)
            {
                $childNode = $this->addChild($firstChar);
            }
            $childNode->addSuffix($remnant);
        }
        else
        {
            $this->finishesWord = TRUE;
        }
    }


    function hasSuffix($suffixArray)
    {
        if(!empty($suffixArray))
        {
            $firstChar = $suffixArray[0];

            $childNode = $this->getChild($firstChar);
            if($childNode == FALSE)
            {
                return FALSE;
            }
            else
            {
                $remnant = array_slice($suffixArray, 1);
                return $childNode->hasSuffix($remnant);
            }
        }
        else
        {
            return TRUE;
        }
    }

    function getChild($childString)
    {
        if(is_array($this->children))
        {
            foreach ($this->children as $child)
            {
                if($child->value === $childString)
                {
                    return $child;
                }
            }
        }
        return FALSE;
    }

    function addChild($childString)
    {
        if(is_array($this->children))
        {
            foreach($this->children as $child)
            {
                if($child->value === $childString)
                {
                    return $child;
                }
            }
        }

        $child = new KTrieNode($childString);
        $this->children[] = $child;

        return $child;
    }

    function printStructure($level)
    {
        for($i=0; $i<$level; $i++)
        {
            echo ".";
        }
        echo $this->value.'<br/>';
        if(is_array($this->children))
        {
            foreach($this->children as $child)
            {
                $child->printStructure($level + 1);
            }
        }
    }

    function printWords($prefix)
    {
        if($this->finishesWord)
        {
            echo $prefix.$this->value.'<br/>';
        }

        if(is_array($this->children))
        {
            foreach($this->children as $child)
            {
                $child->printWords($prefix.$this->value);
            }
        }
    }
}
