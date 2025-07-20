#!/usr/bin/env python3
"""
WhatsApp Chat Text Analyzer
Analyzes text for word frequency and emoji usage
"""

import sys
import json
import re
from collections import Counter
from typing import List, Tuple, Dict, Any
import emoji

def extract_emojis(text: str) -> List[str]:
    """Extract all emojis from text"""
    emoji_pattern = re.compile(
        "["
        "\U0001F600-\U0001F64F"  # emoticons
        "\U0001F300-\U0001F5FF"  # symbols & pictographs
        "\U0001F680-\U0001F6FF"  # transport & map symbols
        "\U0001F1E0-\U0001F1FF"  # flags (iOS)
        "\U00002702-\U000027B0"
        "\U000024C2-\U0001F251"
        "\U0001F900-\U0001F9FF"  # supplemental symbols
        "\U0001F018-\U0001F270"
        "]+", 
        flags=re.UNICODE
    )
    
    emojis = emoji_pattern.findall(text)
    
    # Also try using emoji library for better detection
    try:
        import emoji as emoji_lib
        # Extract emojis using emoji library
        emoji_chars = []
        for char in text:
            if char in emoji_lib.UNICODE_EMOJI['en']:
                emoji_chars.append(char)
        emojis.extend(emoji_chars)
    except:
        pass
    
    return emojis

def clean_text(text: str) -> str:
    """Clean text by removing URLs, special characters, etc."""
    # Remove URLs
    text = re.sub(r'http\S+|www\S+|https\S+', '', text, flags=re.MULTILINE)
    
    # Remove email addresses
    text = re.sub(r'\S+@\S+', '', text)
    
    # Remove phone numbers
    text = re.sub(r'\+?\d[\d\s\-\(\)]+', '', text)
    
    # Remove special WhatsApp messages
    whatsapp_patterns = [
        r'<Media omitted>',
        r'image omitted',
        r'video omitted',
        r'audio omitted',
        r'document omitted',
        r'sticker omitted',
        r'This message was deleted',
        r'You deleted this message',
        r'Messages to this chat and calls are now secured with end-to-end encryption',
        r'.+ added .+',
        r'.+ left',
        r'.+ joined using this group\'s invite link',
        r'.+ changed the subject to',
        r'.+ changed this group\'s icon',
        r'.+ removed .+',
    ]
    
    for pattern in whatsapp_patterns:
        text = re.sub(pattern, '', text, flags=re.IGNORECASE)
    
    return text

def extract_words(text: str) -> List[str]:
    """Extract and clean words from text"""
    # Clean the text first
    text = clean_text(text)
    
    # Remove emojis for word analysis
    text = re.sub(r'[\U0001F600-\U0001F64F\U0001F300-\U0001F5FF\U0001F680-\U0001F6FF\U0001F1E0-\U0001F1FF\U00002702-\U000027B0\U000024C2-\U0001F251\U0001F900-\U0001F9FF]+', ' ', text)
    
    # Extract words (letters only, minimum length 2)
    words = re.findall(r'\b[a-zA-Z]{2,}\b', text.lower())
    
    # Common stop words to filter out
    stop_words = {
        'the', 'be', 'to', 'of', 'and', 'a', 'in', 'that', 'have', 'i', 'it', 'for', 'not', 'on', 'with', 'he',
        'as', 'you', 'do', 'at', 'this', 'but', 'his', 'by', 'from', 'they', 'we', 'say', 'her', 'she', 'or',
        'an', 'will', 'my', 'one', 'all', 'would', 'there', 'their', 'what', 'so', 'up', 'out', 'if', 'about',
        'who', 'get', 'which', 'go', 'me', 'when', 'make', 'can', 'like', 'time', 'no', 'just', 'him', 'know',
        'take', 'people', 'into', 'year', 'your', 'good', 'some', 'could', 'them', 'see', 'other', 'than', 'then',
        'now', 'look', 'only', 'come', 'its', 'over', 'think', 'also', 'back', 'after', 'use', 'two', 'how', 'our',
        'work', 'first', 'well', 'way', 'even', 'new', 'want', 'because', 'any', 'these', 'give', 'day', 'most', 'us',
        'is', 'was', 'are', 'been', 'has', 'had', 'were', 'said', 'each', 'did', 'get', 'may', 'old', 'see', 'way',
        'who', 'boy', 'did', 'its', 'let', 'put', 'too', 'old', 'any', 'ago', 'off', 'far', 'set', 'own', 'under',
        'last', 'very', 'what', 'much', 'where', 'right', 'still', 'try', 'kind', 'hand', 'eye', 'ask', 'felt',
        'such', 'tell', 'try', 'leave', 'call', 'went', 'look', 'right', 'move', 'thing', 'place', 'sure', 'end',
        'why', 'turn', 'every', 'start', 'might', 'story', 'saw', 'far', 'sea', 'draw', 'left', 'late', 'run',
        'while', 'press', 'close', 'night', 'real', 'life', 'few', 'stop', 'open', 'seem', 'together', 'next',
        'white', 'children', 'got', 'walk', 'example', 'begin', 'took', 'river', 'mountain', 'cut', 'young',
        'talk', 'soon', 'list', 'song', 'leave', 'family', 'body', 'music', 'color', 'stand', 'sun', 'questions',
        'fish', 'area', 'mark', 'dog', 'horse', 'birds', 'problem', 'complete', 'room', 'knew', 'since', 'ever',
        'piece', 'told', 'usually', 'didn', 'friends', 'easy', 'heard', 'order', 'red', 'door', 'sure', 'become',
        'top', 'ship', 'across', 'today', 'during', 'short', 'better', 'best', 'however', 'low', 'hours', 'black',
        'products', 'happened', 'whole', 'measure', 'remember', 'early', 'waves', 'reached', 'listen', 'wind',
        'rock', 'space', 'covered', 'fast', 'several', 'hold', 'himself', 'toward', 'five', 'step', 'morning',
        'passed', 'vowel', 'true', 'hundred', 'against', 'pattern', 'numeral', 'table', 'north', 'slowly', 'money',
        'map', 'farm', 'pulled', 'draw', 'voice', 'seen', 'cold', 'cried', 'plan', 'notice', 'south', 'sing',
        'war', 'ground', 'fall', 'king', 'town', 'unit', 'figure', 'certain', 'field', 'travel', 'wood', 'fire',
        'upon', 'done', 'english', 'road', 'half', 'ten', 'fly', 'gave', 'box', 'finally', 'wait', 'correct',
        'oh', 'quickly', 'person', 'became', 'shown', 'minutes', 'strong', 'verb', 'stars', 'eat', 'front',
        'feel', 'fact', 'inches', 'street', 'decided', 'contain', 'course', 'surface', 'produced', 'building',
        'ocean', 'class', 'note', 'nothing', 'rest', 'carefully', 'scientists', 'inside', 'wheels', 'stay',
        'green', 'known', 'island', 'week', 'less', 'machine', 'base', 'ago', 'stood', 'plane', 'system',
        'behind', 'ran', 'round', 'boat', 'game', 'force', 'brought', 'heat', 'nothing', 'quite', 'brought',
        'lot', 'gold', 'sit', 'meet', 'third', 'months', 'paragraph', 'raised', 'represent', 'soft', 'whether',
        'clothes', 'flowers', 'shall', 'teacher', 'held', 'describe', 'drive', 'cross', 'speak', 'solve',
        'appear', 'metal', 'son', 'either', 'ice', 'sleep', 'village', 'factors', 'result', 'jumped', 'snow',
        'ride', 'care', 'floor', 'hill', 'pushed', 'baby', 'buy', 'century', 'outside', 'everything', 'tall',
        'already', 'instead', 'phrase', 'soil', 'bed', 'copy', 'free', 'hope', 'spring', 'case', 'laughed',
        'nation', 'quite', 'type', 'themselves', 'temperature', 'bright', 'lead', 'everyone', 'method', 'section',
        'lake', 'iron', 'within', 'dictionary', 'hair', 'age', 'amount', 'scale', 'pounds', 'although', 'per',
        'broken', 'moment', 'tiny', 'possible', 'gold', 'milk', 'quiet', 'natural', 'lot', 'stone', 'act',
        'build', 'middle', 'speed', 'count', 'cat', 'someone', 'sail', 'rolled', 'bear', 'wonder', 'smiled',
        'angle', 'fraction', 'africa', 'killed', 'melody', 'bottom', 'trip', 'hole', 'poor', 'let', 'fight',
        'surprise', 'french', 'died', 'beat', 'exactly', 'remain', 'dress', 'iron', 'couldn', 'fingers',
        'row', 'least', 'catch', 'climbed', 'wrote', 'shouted', 'continued', 'itself', 'else', 'plains',
        'gas', 'england', 'burning', 'design', 'joined'
    }
    
    # Filter out stop words and very short words
    filtered_words = [word for word in words if word not in stop_words and len(word) > 2]
    
    return filtered_words

def analyze_text(file_path: str) -> Dict[str, Any]:
    """Main analysis function"""
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            text = file.read()
    except Exception as e:
        return {"error": f"Failed to read file: {str(e)}"}
    
    # Extract and count words
    words = extract_words(text)
    word_counts = Counter(words)
    top_words = word_counts.most_common(30)
    
    # Extract and count emojis
    emojis = extract_emojis(text)
    emoji_counts = Counter(emojis)
    top_emojis = emoji_counts.most_common(20)
    
    return {
        "top_words": top_words,
        "top_emojis": top_emojis,
        "total_unique_words": len(word_counts),
        "total_words": len(words),
        "total_unique_emojis": len(emoji_counts),
        "total_emojis": len(emojis)
    }

def main():
    if len(sys.argv) != 2:
        print(json.dumps({"error": "Usage: python analyzer.py <file_path>"}))
        sys.exit(1)
    
    file_path = sys.argv[1]
    result = analyze_text(file_path)
    print(json.dumps(result, ensure_ascii=False))

if __name__ == "__main__":
    main()