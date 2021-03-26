import base64
import zlib

strings = ["One", "Two", "Three", "Four", "Five"]


def create(name):
    for i in range(5):
        st = "in\\My_File_" + strings[i] + "_" + name + "_Combined.txt"
        f2 = open(st)
        unzipped = f2.read()
        f2.close()

        unzipped = bytes(unzipped, "utf-8")

        jsonrepre = zlib.compress(unzipped, 9)
        befor = base64.b64encode(jsonrepre)

        final_file = open("out\\final_" + name + "_blueprint_" + strings[i] + "Rote.txt", "w+")
        final = str(befor)
        final = final[2:][:-1]
        final_file.write("0" + final)

        print("Finished the blueprint \"" + name + "_" + strings[i] + "\"")


print("Starting with the darts!")
create("Kite")
print("Finished the darts")

print("Starting with the Kites!")
create("Dart")
print("Finished the kites")
