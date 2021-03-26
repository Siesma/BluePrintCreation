# This is a sample Python script.

# Press Umschalt+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.

import base64
import zlib

for i in range(5):
    strings = ["One", "Two", "Three", "Four", "Five"]
    st = "My_File_" + strings[i] + "_Combined.txt"
    f2 = open(st)
    unzipped = f2.read()
    f2.close()

    unzipped = bytes(unzipped, "utf-8")

    jsonrepre = zlib.compress(unzipped, 9)
    befor = base64.b64encode(jsonrepre)

    final_file = open("final_blueprint_" + strings[i] + "Rote.txt", "w+")

    # apparently this doesnt work. It should remove the first two and the last character of the String.
    # So this has to be done manually.
    befor = befor[2:][:-3]

    final_file.write(str(befor))

    print("Finished the blueprint \"" + strings[i] + "\"")
