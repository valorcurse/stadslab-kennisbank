package kennisbank.utils

class Utils {

	public static String humanReadableByteCount(long fileSize) {
		def labels = [ ' bytes', 'KB', 'MB', 'GB', 'TB' ]
		def size = fileSize
		def label = labels.find {
			if ( size < 1024 ) {
				true
			}
			else {
				size /= 1024  
				false
			}
		}
		return "${new java.text.DecimalFormat('0.##').format( size )} $label"
	}
}