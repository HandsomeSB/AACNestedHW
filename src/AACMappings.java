import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import edu.grinnell.csc207.util.AssociativeArray;
import java.util.NoSuchElementException;

/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * an interactions.
 * 
 * @author Catie Baker & YOUR NAME HERE
 *
 */
public class AACMappings implements AACPage {

	private AssociativeArray<String, AACCategory> categories = new AssociativeArray<String, AACCategory>();
	private AACCategory currentCategory;
	private final AACCategory home;
	
	/**
	 * Creates a set of mappings for the AAC based on the provided
	 * file. The file is read in to create categories and fill each
	 * of the categories with initial items. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * @param filename the name of the file that stores the mapping information
	 */
	public AACMappings(String filename) {
		this.home = new AACCategory("");
		try {
			this.categories.set("", this.home);
		} catch (Exception e) {
			// Do nothing
		}
		
		try (Scanner scanner = new Scanner(new File(filename))) {
			AACCategory newCategory = home; // pointer to constructing category
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine(); // Read the next line
                String[] split = line.split(" ", 2);

				if(line.charAt(0) == '>') { // add new item in category
					split[0] = split[0].substring(1);
					newCategory.addItem(split[0], split[1]);
				} else {  // add new category
					newCategory = new AACCategory(split[1]);
					try {
						this.categories.set(newCategory.getCategory(), newCategory);
						this.home.addItem(split[0], split[1]);
					} catch (Exception e) {

					}
				}
            }

			// Test
			for(String k :categories.keys(String.class)) { 
				try {
					System.out.println(categories.get(k).toString());
				} catch (Exception e) {
					System.out.println(k + " not found");
				}
				
			}

        } catch (FileNotFoundException e) {
            e.printStackTrace(); // Handle file not found exception
        }

		this.currentCategory = home;
	}
	
	/**
	 * Given the image location selected, it determines the action to be
	 * taken. This can be updating the information that should be displayed
	 * or returning text to be spoken. If the image provided is a category, 
	 * it updates the AAC's current category to be the category associated 
	 * with that image and returns the empty string. If the AAC is currently
	 * in a category and the image provided is in that category, it returns
	 * the text to be spoken.
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise
	 * it returns the empty string
	 * @throws NoSuchElementException if the image provided is not in the current 
	 * category
	 */
	public String select(String imageLoc) throws NoSuchElementException {
		try { 
			String text = this.currentCategory.select(imageLoc);
			if(this.currentCategory.getCategory().equals("")) { //if currently at home
				this.currentCategory = this.categories.get(text);
				return "";
			} else { 
				return text;
			}
		} catch (Exception e){ 
			throw new NoSuchElementException();
		}
	}
	
	/**
	 * Provides an array of all the images in the current category
	 * @return the array of images in the current category; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
		return this.currentCategory.getImageLocs();
	}
	
	/**
	 * Resets the current category of the AAC back to the default
	 * category
	 */
	public void reset() {
		this.currentCategory = this.home;
	}
	
	
	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * 
	 * @param filename the name of the file to write the
	 * AAC mapping to
	 */
	public void writeToFile(String filename) {
		// for every itemName in home
		//		print(loc + categoryname)
		//		go into that category and print everything out
		String[] categoryImgLocs = home.getImageLocs();
		for(String loc : categoryImgLocs) { 
			try {
				AACCategory category = this.categories.get(home.select(loc));
				System.out.println(loc + " " + category.getCategory()); //STUB
				for(String itemLoc : category.getImageLocs()) {
					System.out.println(">" + itemLoc + " " + category.select(itemLoc)); //STUB
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}
	
	/**
	 * Adds the mapping to the current category (or the default category if
	 * that is the current category)
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
		this.currentCategory.addItem(imageLoc, text);
		if(this.currentCategory == this.home) { 
			try {
				this.categories.set(text, new AACCategory(text));
			} catch (Exception e) {
				// TODO: handle exception
			} // try/catch
		} // if home
	}


	/**
	 * Gets the name of the current category
	 * @return returns the current category or the empty string if 
	 * on the default category
	 */
	public String getCategory() {
		return this.currentCategory.getCategory();
	}

	/**
	 * Given an image, it will return true if it is a category image and false otherwise.
	 * @return
	 */
	public boolean isCategory(String imgLoc) { 
		try {
			this.home.select(imgLoc);
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	/**
	 * Determines if the provided image is in the set of images that
	 * can be displayed and false otherwise
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that
	 * can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		return this.currentCategory.hasImage(imageLoc);
	}
}
