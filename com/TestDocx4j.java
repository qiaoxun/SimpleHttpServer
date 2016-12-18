package com.qiao.docx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTRel;
import org.docx4j.wml.Color;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.STHint;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Text;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;

//http://www.javatechblog.com/java/create-header-and-footer-for-word-documents-using-docx4j/
public class TestDocx4j {
	public static void main(String[] args) {
		try {
			String path = "/home/joey/study/test";
			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
			// MainDocumentPart wordDocumentPart =
			// wordMLPackage.getMainDocumentPart();
			// wordDocumentPart.addParagraphOfText("Manipulating Word document
			// with docx4j");
			// // Save it
			// wordMLPackage.save(new File(path + "/helloworld.docx"));

			File file = new File(path + "/HelloWord11.docx");
			InputStream in = new FileInputStream(file);
			// Load the word document into memory and create an in-object memory
			// representation
			wordMLPackage = WordprocessingMLPackage.load(in);
			createHeaderPart(wordMLPackage);
			addFooterToDocument(wordMLPackage, "1.0");
			wordMLPackage.save(file);

			System.out.println("Finished editing the word document");
		} catch (Docx4JException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add Footer to the document
	 * 
	 * @param wordMLPackage
	 * @param docVersionNumber
	 * @throws InvalidFormatException
	 */
	private static void addFooterToDocument(WordprocessingMLPackage wordMLPackage, String docVersionNumber)
			throws InvalidFormatException {
		ObjectFactory factory = Context.getWmlObjectFactory();
		Relationship relationship = createFooterPart(wordMLPackage, factory, docVersionNumber);
		createFooterReference(relationship, wordMLPackage, factory);
	}

	/**
	 * Add header to the document
	 * 
	 * @param wordMLPackage
	 * @throws InvalidFormatException
	 */
	private static void createHeaderPart(WordprocessingMLPackage wordMLPackage) throws InvalidFormatException {
		ObjectFactory factory = Context.getWmlObjectFactory();
		HeaderPart headerPart = new HeaderPart();
		headerPart.setPackage(wordMLPackage);

		Hdr header = factory.createHdr();
		P paragraph = factory.createP();
		R run = factory.createR();
		/*
		 * Change the font size to 8 points(the font size is defined to be in
		 * half-point size so set the value as 16).
		 */
		RPr rpr = new RPr();
		HpsMeasure size = new HpsMeasure();
		size.setVal(BigInteger.valueOf(16));
		rpr.setSz(size);
		run.setRPr(rpr);
		Text text = new Text();
		text.setValue("This is a Header");
		run.getContent().add(text);
		paragraph.getContent().add(run);
		header.getContent().add(paragraph);
		headerPart.setJaxbElement(header);

		Relationship relationship = wordMLPackage.getMainDocumentPart().addTargetPart(headerPart);
		List<SectionWrapper> sections = wordMLPackage.getDocumentModel().getSections();
		SectPr sectionProperties = sections.get(sections.size() - 1).getSectPr();
		// There is always a section wrapper, but it might not contain a sectPr
		if (sectionProperties == null) {
			sectionProperties = factory.createSectPr();
			wordMLPackage.getMainDocumentPart().addObject(sectionProperties);
			sections.get(sections.size() - 1).setSectPr(sectionProperties);
		}

		/*
		 * Remove Header if it already exists.
		 */
		List<CTRel> relations = sectionProperties.getEGHdrFtrReferences();
		Iterator<CTRel> relationsItr = relations.iterator();
		while (relationsItr.hasNext()) {
			CTRel relation = relationsItr.next();
			if (relation instanceof HeaderReference) {
				relationsItr.remove();
			}
		}

		HeaderReference headerReference = factory.createHeaderReference();
		headerReference.setId(relationship.getId());
		headerReference.setType(HdrFtrRef.DEFAULT);
		sectionProperties.getEGHdrFtrReferences().add(headerReference);
		HeaderReference firstPageHeaderRef = factory.createHeaderReference();
		firstPageHeaderRef.setId(relationship.getId());
		firstPageHeaderRef.setType(HdrFtrRef.FIRST);
		sectionProperties.getEGHdrFtrReferences().add(firstPageHeaderRef);
	}

	/**
	 * This method creates a footer part and set the package on it. Then we add
	 * some text and add the footer part to the package. Finally we return the
	 * corresponding relationship.
	 * 
	 * @param wordMLPackage
	 *            the word ml package
	 * @param versionNumber
	 * @param documentNumber
	 * @return the relationship
	 * @throws InvalidFormatException
	 *             the invalid format exception
	 */
	private static Relationship createFooterPart(WordprocessingMLPackage wordMLPackage, ObjectFactory factory,
			String docversionNumber) throws InvalidFormatException {
		FooterPart footerPart = new FooterPart();
		footerPart.setPackage(wordMLPackage);
		StringBuilder footerText = new StringBuilder("");
		String documentNumber[] = docversionNumber.split("\\.");
		footerText.append(documentNumber[0]);
		footerText.append("v.");
		footerText.append(documentNumber[1]);
//		footerPart.setJaxbElement(createFooter(footerText.toString(), factory));
		footerPart.setJaxbElement(createFooterWithPageNr(factory));
		return wordMLPackage.getMainDocumentPart().addTargetPart(footerPart);
	}

	/**
	 * First we create a footer, a paragraph, a run and a text. We add the given
	 * given content to the text and add that to the run. The run is then added
	 * to the paragraph, which is in turn added to the footer. Finally we return
	 * the footer.
	 * 
	 * @param content
	 * @return
	 */
	private static Ftr createFooter(String content, ObjectFactory factory) {
		Ftr footer = factory.createFtr();
		P paragraph = factory.createP();
		R run = factory.createR();
		/*
		 * Change the font size to 8 points(the font size is defined to be in
		 * half-point size so set the value as 16).
		 */
		RPr rpr = new RPr();
		HpsMeasure size = new HpsMeasure();
		size.setVal(BigInteger.valueOf(16));
		rpr.setSz(size);
		run.setRPr(rpr);
		Text text = new Text();
		text.setValue(content);
		run.getContent().add(text);
		paragraph.getContent().add(run);
		footer.getContent().add(paragraph);
		P pageNumParagraph = factory.createP();
		addFieldBegin(factory, pageNumParagraph);
		addPageNumberField(factory, pageNumParagraph);
		addFieldEnd(factory, pageNumParagraph);
		footer.getContent().add(pageNumParagraph);
		return footer;
	}

	/**
	 * Creating the page number field is nearly the same as creating the field
	 * in the TOC example. The only difference is in the value. We use the PAGE
	 * command, which prints the number of the current page, together with the
	 * MERGEFORMAT switch, which indicates that the current formatting should be
	 * preserved when the field is updated.
	 * 
	 * @param paragraph
	 */
	private static void addPageNumberField(ObjectFactory factory, P paragraph) {
		R run = factory.createR();
		PPr ppr = new PPr();
		Jc jc = new Jc();
		jc.setVal(JcEnumeration.CENTER);
		ppr.setJc(jc);
		paragraph.setPPr(ppr);
		Text txt = new Text();
		txt.setSpace("preserve");
		txt.setValue("PAGE   \\* MERGEFORMAT");
		// txt.setValue("第{PAGE}页/共{NUMPAGES}页");
		run.getContent().add(factory.createRInstrText(txt));
		paragraph.getContent().add(run);
	}

	/**
	 * Every fields needs to be delimited by complex field characters. This
	 * method adds the delimiter that precedes the actual field to the given
	 * paragraph.
	 * 
	 * @param paragraph
	 */
	private static void addFieldBegin(ObjectFactory factory, P paragraph) {
		R run = factory.createR();
		FldChar fldchar = factory.createFldChar();
		fldchar.setFldCharType(STFldCharType.BEGIN);
		run.getContent().add(fldchar);
		paragraph.getContent().add(run);
	}

	/**
	 * Every fields needs to be delimited by complex field characters. This
	 * method adds the delimiter that follows the actual field to the given
	 * paragraph.
	 * 
	 * @param paragraph
	 */
	private static void addFieldEnd(ObjectFactory factory, P paragraph) {
		FldChar fldcharend = factory.createFldChar();
		fldcharend.setFldCharType(STFldCharType.END);
		R run3 = factory.createR();
		run3.getContent().add(fldcharend);
		paragraph.getContent().add(run3);
	}

	/**
	 * First we retrieve the document sections from the package. As we want to
	 * add a footer, we get the last section and take the section properties
	 * from it. The section is always present, but it might not have properties,
	 * so we check if they exist to see if we should create them. If they need
	 * to be created, we do and add them to the main document part and the
	 * section. Then we create a reference to the footer, give it the id of the
	 * relationship, set the type to header/footer reference and add it to the
	 * collection of references to headers and footers in the section
	 * properties.
	 * 
	 * @param relationship
	 * @param wordMLPackage
	 * @param factory
	 */
	private static void createFooterReference(Relationship relationship, WordprocessingMLPackage wordMLPackage,
			ObjectFactory factory) {
		List<SectionWrapper> sections = wordMLPackage.getDocumentModel().getSections();
		SectPr sectionProperties = sections.get(sections.size() - 1).getSectPr();
		// There is always a section wrapper, but it might not contain a sectPr
		if (sectionProperties == null) {
			sectionProperties = factory.createSectPr();
			wordMLPackage.getMainDocumentPart().addObject(sectionProperties);
			sections.get(sections.size() - 1).setSectPr(sectionProperties);
		}

		/*
		 * Remove footer if it already exists.
		 */
		List<CTRel> relations = sectionProperties.getEGHdrFtrReferences();
		Iterator<CTRel> relationsItr = relations.iterator();
		while (relationsItr.hasNext()) {
			CTRel relation = relationsItr.next();
			if (relation instanceof FooterReference) {
				relationsItr.remove();
			}
		}

		FooterReference footerReference = factory.createFooterReference();
		footerReference.setId(relationship.getId());
		footerReference.setType(HdrFtrRef.DEFAULT);
		sectionProperties.getEGHdrFtrReferences().add(footerReference);
		FooterReference firstPagefooterRef = factory.createFooterReference();
		firstPagefooterRef.setId(relationship.getId());
		firstPagefooterRef.setType(HdrFtrRef.FIRST);
		sectionProperties.getEGHdrFtrReferences().add(firstPagefooterRef);
	}

	public static Ftr createFooterWithPageNr(ObjectFactory factory) {
		Ftr ftr = factory.createFtr();
		P paragraph = factory.createP();
		RPr fontRPr = getRPr(factory, "宋体", "000000", "20", STHint.EAST_ASIA, false, false, false, false);
		R run = factory.createR();
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);

		addPageTextField(factory, paragraph, "第");
		addFieldBegin(factory, paragraph);
		addPageNumberField(factory, paragraph);
		addFieldEnd(factory, paragraph);
		addPageTextField(factory, paragraph, "页");

		addPageTextField(factory, paragraph, " / 共");
		addFieldBegin(factory, paragraph);
		addTotalPageNumberField(factory, paragraph);
		addFieldEnd(factory, paragraph);
		addPageTextField(factory, paragraph, "页");
		setParagraphAlign(factory, paragraph, JcEnumeration.CENTER);
		ftr.getContent().add(paragraph);
		return ftr;
	}

	public static void addTotalPageNumberField(ObjectFactory factory, P paragraph) {
		R run = factory.createR();
		Text txt = new Text();
		txt.setSpace("preserve");
		txt.setValue("NUMPAGES  \\* MERGEFORMAT ");
		run.getContent().add(factory.createRInstrText(txt));
		paragraph.getContent().add(run);
	}

	private static void addPageTextField(ObjectFactory factory, P paragraph, String value) {
		R run = factory.createR();
		Text txt = new Text();
		txt.setSpace("preserve");
		txt.setValue(value);
		run.getContent().add(txt);
		paragraph.getContent().add(run);
	}

	// 水平对齐方式
	// TODO 垂直对齐没写
	public static void setParagraphAlign(ObjectFactory factory, P p, JcEnumeration jcEnumeration) {
		PPr pPr = p.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new Jc();
		}
		jc.setVal(jcEnumeration);
		pPr.setJc(jc);
		p.setPPr(pPr);
	}

	/**
	 * 创建字体
	 * 
	 * @param isBlod
	 *            粗体
	 * @param isUnderLine
	 *            下划线
	 * @param isItalic
	 *            斜体
	 * @param isStrike
	 *            删除线
	 */
	public static RPr getRPr(ObjectFactory factory, String fontFamily, String colorVal, String fontSize, STHint sTHint,
			boolean isBlod, boolean isUnderLine, boolean isItalic, boolean isStrike) {
		RPr rPr = factory.createRPr();
		RFonts rf = new RFonts();
		rf.setHint(sTHint);
		rf.setAscii(fontFamily);
		rf.setHAnsi(fontFamily);
		rPr.setRFonts(rf);

		BooleanDefaultTrue bdt = factory.createBooleanDefaultTrue();
		rPr.setBCs(bdt);
		if (isBlod) {
			rPr.setB(bdt);
		}
		if (isItalic) {
			rPr.setI(bdt);
		}
		if (isStrike) {
			rPr.setStrike(bdt);
		}
		if (isUnderLine) {
			U underline = new U();
			underline.setVal(UnderlineEnumeration.SINGLE);
			rPr.setU(underline);
		}

		Color color = new Color();
		color.setVal(colorVal);
		rPr.setColor(color);

		HpsMeasure sz = new HpsMeasure();
		sz.setVal(new BigInteger(fontSize));
		rPr.setSz(sz);
		rPr.setSzCs(sz);
		return rPr;
	}

}