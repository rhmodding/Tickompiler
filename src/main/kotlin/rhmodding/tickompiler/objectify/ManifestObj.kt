package rhmodding.tickompiler.objectify


class ManifestObj {
    var version: Int = -1
    var bin: BinObject = BinObject()
    var tempo: TempoObject = TempoObject()
}

class BinObject {
    var size: Int = 0
}

class TempoObject {
    var size: Int = 0
}
