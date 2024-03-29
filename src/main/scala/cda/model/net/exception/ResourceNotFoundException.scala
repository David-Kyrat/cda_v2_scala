package cda.model.net.exception

trait ResourceNotFoundException extends Exception {
    // This is called a "self annotation". You can use "self" or "dog" or whatever you want.
    // It requires that those who extend this trait must also extend Throwable, or a subclass of it.
    self: Throwable =>
}

object ResourceNotFoundException {

    def fmtErr(resName: String) = f"$resName was not found. If this course exists and was not removed, please check its spelling (or your internet connection) and retry."
}

