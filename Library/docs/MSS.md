## MSS (Markdown Style Sheet)

The MSS format is a JSON document describing the styles (colors and fonts) to use for different sections of a markdown document (standard text, heading, bold, code, etc). It contains 3 main sections: front page, TOC, pages. There is a _default.mss_ embedded in the jar that is used if no external mss files is provided. The default MSS should be compatible with styles used in previous versions. 

Currently the MSS file is only usewd by the PDF generator. But it could also be relevant for other formats, like word if I ever add such a generator. I gladly take a pull request if anybody wants to do that :-). 

The best way to describe the MSS file is to show the _default.mss_ file:

    {

This section is specific to PDF files, and specifies ttf, otf, and other font types supported by iText. For the _internal_ fonts "HELVETICA" or "COURIER" is specified as "family", but to use a font specified here, just use the name set as "family" here. If you are using an exernal Helvetica font specified here, dont call it just "HELVETICA" since there will be confusion!

A best effort is used to resolve the font in "path":. If the specified path does not match relative to current directory then it will try the parent directory and so on all the way upp to the filesytem root. 

      "pdf": {
        "extFonts": [
          {
            "family": "MDD-EXAMPLE",
            "encoding": "UTF-8",
            "path": "/fonts/ttf/some-font.ttf"
          }
        ]
      },

The "colors" section just provide names for colors. This list was taken from the default color names for CSS colors, with the exception of the first 3. Any color specification in secitons below that does not contain any ":" character will be taken as a name and looked up here.

      "colors": {
        "white": "255:255:255",
        "black": "0:0:0",
        "mddgrey": "128:128:128",
        "AliceBlue": "F0F8FF",
        "AntiqueWhite": "FAEBD7",
        "Aqua": "00FFFF",
        "Aquamarine": "7FFFD4",
        "Azure": "F0FFFF",
        "Beige": "F5F5DC",
        "Bisque": "FFE4C4",
        "Black": "000000",
        "BlanchedAlmond": "FFEBCD",
        "Blue": "0000FF",
        "BlueViolet": "8A2BE2",
        "Brown": "A52A2A",
        "BurlyWood": "DEB887",
        "CadetBlue": "5F9EA0",
        "Chartreuse": "7FFF00",
        "Chocolate": "D2691E",
        "Coral": "FF7F50",
        "CornflowerBlue": "6495ED",
        "Cornsilk": "FFF8DC",
        "Crimson": "DC143C",
        "Cyan": "00FFFF",
        "DarkBlue": "00008B",
        "DarkCyan": "008B8B",
        "DarkGoldenRod": "B8860B",
        "DarkGray": "A9A9A9",
        "DarkGreen": "006400",
        "DarkKhaki": "BDB76B",
        "DarkMagenta": "8B008B",
        "DarkOliveGreen": "556B2F",
        "DarkOrange": "FF8C00",
        "DarkOrchid": "9932CC",
        "DarkRed": "8B0000",
        "DarkSalmon": "E9967A",
        "DarkSeaGreen": "8FBC8F",
        "DarkSlateBlue": "483D8B",
        "DarkSlateGray": "2F4F4F",
        "DarkTurquoise": "00CED1",
        "DarkViolet": "9400D3",
        "DeepPink": "FF1493",
        "DeepSkyBlue": "00BFFF",
        "DimGray": "696969",
        "DodgerBlue": "1E90FF",
        "FireBrick": "B22222",
        "FloralWhite": "FFFAF0",
        "ForestGreen": "228B22",
        "Fuchsia": "FF00FF",
        "Gainsboro": "DCDCDC",
        "GhostWhite": "F8F8FF",
        "Gold": "FFD700",
        "GoldenRod": "DAA520",
        "Gray": "808080",
        "Green": "008000",
        "GreenYellow": "ADFF2F",
        "HoneyDew": "F0FFF0",
        "HotPink": "FF69B4",
        "IndianRed": "CD5C5C",
        "Indigo": "4B0082",
        "Ivory": "FFFFF0",
        "Khaki": "F0E68C",
        "Lavender": "E6E6FA",
        "LavenderBlush": "FFF0F5",
        "LawnGreen": "7CFC00",
        "LemonChiffon": "FFFACD",
        "LightBlue": "ADD8E6",
        "LightCoral": "F08080",
        "LightCyan": "E0FFFF",
        "LightGoldenRodYellow": "FAFAD2",
        "LightGray": "D3D3D3",
        "LightGreen": "90EE90",
        "LightPink": "FFB6C1",
        "LightSalmon": "FFA07A",
        "LightSeaGreen": "20B2AA",
        "LightSkyBlue": "87CEFA",
        "LightSlateGray": "778899",
        "LightSteelBlue": "B0C4DE",
        "LightYellow": "FFFFE0",
        "Lime": "00FF00",
        "LimeGreen": "32CD32",
        "Linen": "FAF0E6",
        "Magenta": "FF00FF",
        "Maroon": "800000",
        "MediumAquaMarine": "66CDAA",
        "MediumBlue": "0000CD",
        "MediumOrchid": "BA55D3",
        "MediumPurple": "9370DB",
        "MediumSeaGreen": "3CB371",
        "MediumSlateBlue": "7B68EE",
        "MediumSpringGreen": "00FA9A",
        "MediumTurquoise": "48D1CC",
        "MediumVioletRed": "C71585",
        "MidnightBlue": "191970",
        "MintCream": "F5FFFA",
        "MistyRose": "FFE4E1",
        "Moccasin": "FFE4B5",
        "NavajoWhite": "FFDEAD",
        "Navy": "000080",
        "OldLace": "FDF5E6",
        "Olive": "808000",
        "OliveDrab": "6B8E23",
        "Orange": "FFA500",
        "OrangeRed": "FF4500",
        "Orchid": "DA70D6",
        "PaleGoldenRod": "EEE8AA",
        "PaleGreen": "98FB98",
        "PaleTurquoise": "AFEEEE",
        "PaleVioletRed": "DB7093",
        "PapayaWhip": "FFEFD5",
        "PeachPuff": "FFDAB9",
        "Peru": "CD853F",
        "Pink": "FFC0CB",
        "Plum": "DDA0DD",
        "PowderBlue": "B0E0E6",
        "Purple": "800080",
        "RebeccaPurple": "663399",
        "Red": "FF0000",
        "RosyBrown": "BC8F8F",
        "RoyalBlue": "4169E1",
        "SaddleBrown": "8B4513",
        "Salmon": "FA8072",
        "SandyBrown": "F4A460",
        "SeaGreen": "2E8B57",
        "SeaShell": "FFF5EE",
        "Sienna": "A0522D",
        "Silver": "C0C0C0",
        "SkyBlue": "87CEEB",
        "SlateBlue": "6A5ACD",
        "SlateGray": "708090",
        "Snow": "FFFAFA",
        "SpringGreen": "00FF7F",
        "SteelBlue": "4682B4",
        "Tan": "D2B48C",
        "Teal": "008080",
        "Thistle": "D8BFD8",
        "Tomato": "FF6347",
        "Turquoise": "40E0D0",
        "Violet": "EE82EE",
        "Wheat": "F5DEB3",
        "White": "FFFFFF",
        "WhiteSmoke": "F5F5F5",
        "Yellow": "FFFF00",
        "YellowGreen": "9ACD32"
      },

This section deals with document styles. It has 3 sections: "pages", "front\_page", and "toc". If a style is not set in a specific section it will fall back to what is specified in a more general section.

      "document": {
        "color": "black",
        "background": "white",
        "family": "HELVETICA",
        "size": 10,
        "style": "Normal",
        "pages": {
          "block_quote": {
            "style": "Italic",
            "color": "mddgrey"
          },
          "h1": {
            "size": 20,
            "style": "BOLD"
          },
          "h2": {
            "size": 18,
            "style": "BOLD",
            "hr": true
          },
          "h3": {
            "size": 16,
            "style": "BOLD"
          },
          "h4": {
            "size": 14,
            "style": "BOLD"
          },
          "h5": {
            "size": 12,
            "style": "BOLD"
          },
          "h6": {
            "size": 10,
            "style": "BOLD"
          },
          "emphasis": {
            "style": "ITALIC"
          },
          "strong": {
            "style": "BOLD"
          },
          "code": {
            "family": "COURIER",
            "size": 9,
            "color": "64:64:64"
          },
          "anchor": {
            "color": "128:128:128"
          },
          "list_item": {
          },
          "footer": {
            "size": 8
          }
        },
    
        "divs": {
          "mdd-example": {
            "color": "white",
            "background": "black",
            "block_quote": {
              "family": "COURIER",
              "color": "120:120:120",
              "background": "10:11:12"
            }
          }
        }
      },

Note the "label": in "version" and "author"! This is what is used for these 2 texts. So it can be translated to another language if wanted.

      "front_page": {
        "color": "0:0:0",
        "background": "255:255:255",
        "family": "HELVETICA",
        "size": 10,
        "style": "NORMAL",
        "title": {
          "size": 25,
          "style": "UNDERLINE"
        },
        "subject": {
          "size": 15
        },
        "version": {
          "size": 12,
          "label": "Version:"
        },
        "copyright": {
        },
        "author": {
          "size": 12,
          "label": "Author:"
        }
      },
    
      "toc": {
        "color": "0:0:0",
        "background": "255:255:255",
        "family": "HELVETICA",
        "size": 9,
        "style": "NORMAL",
        "toc": {
        },
        "h1": {
          "style": "BOLD"
        },
        "h2": {
        },
        "h3": {
        },
        "h4": {
        },
        "h5": {
        },
        "h6": {
        }
      }
    }
    
 