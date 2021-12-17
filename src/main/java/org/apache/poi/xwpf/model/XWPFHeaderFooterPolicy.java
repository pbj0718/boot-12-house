package org.apache.poi.xwpf.model;

import com.microsoft.schemas.office.office.CTLock;
import com.microsoft.schemas.office.office.STConnectType;
import com.microsoft.schemas.vml.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLDocumentPart.RelationPart;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.HdrDocument.Factory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr.Enum;

import java.util.Iterator;

/**
 * 重写poi中的XWPFHeaderFooterPolicy用于生成多行文字水印
 */
public class XWPFHeaderFooterPolicy {
    public static final Enum DEFAULT;
    public static final Enum EVEN;
    public static final Enum FIRST;
    private XWPFDocument doc;
    private XWPFHeader firstPageHeader;
    private XWPFFooter firstPageFooter;
    private XWPFHeader evenPageHeader;
    private XWPFFooter evenPageFooter;
    private XWPFHeader defaultHeader;
    private XWPFFooter defaultFooter;

    public XWPFHeaderFooterPolicy(XWPFDocument doc) {
        this(doc, (CTSectPr) null);
    }

    public XWPFHeaderFooterPolicy(XWPFDocument doc, CTSectPr sectPr) {
        if (sectPr == null) {
            CTBody i = doc.getDocument().getBody();
            sectPr = i.isSetSectPr() ? i.getSectPr() : i.addNewSectPr();
        }

        this.doc = doc;

        CTHdrFtrRef ref;
        POIXMLDocumentPart relatedPart;
        Enum type;
        int var11;
        for (var11 = 0; var11 < sectPr.sizeOfHeaderReferenceArray(); ++var11) {
            ref = sectPr.getHeaderReferenceArray(var11);
            relatedPart = doc.getRelationById(ref.getId());
            XWPFHeader ftr = null;
            if (relatedPart != null && relatedPart instanceof XWPFHeader) {
                ftr = (XWPFHeader) relatedPart;
            }

            try {
                type = ref.getType();
            } catch (XmlValueOutOfRangeException var10) {
                type = STHdrFtr.DEFAULT;
            }

            this.assignHeader(ftr, type);
        }

        for (var11 = 0; var11 < sectPr.sizeOfFooterReferenceArray(); ++var11) {
            ref = sectPr.getFooterReferenceArray(var11);
            relatedPart = doc.getRelationById(ref.getId());
            XWPFFooter var12 = null;
            if (relatedPart != null && relatedPart instanceof XWPFFooter) {
                var12 = (XWPFFooter) relatedPart;
            }

            try {
                type = ref.getType();
            } catch (XmlValueOutOfRangeException var9) {
                type = STHdrFtr.DEFAULT;
            }

            this.assignFooter(var12, type);
        }

    }

    private void assignFooter(XWPFFooter ftr, Enum type) {
        if (type == STHdrFtr.FIRST) {
            this.firstPageFooter = ftr;
        } else if (type == STHdrFtr.EVEN) {
            this.evenPageFooter = ftr;
        } else {
            this.defaultFooter = ftr;
        }

    }

    private void assignHeader(XWPFHeader hdr, Enum type) {
        if (type == STHdrFtr.FIRST) {
            this.firstPageHeader = hdr;
        } else if (type == STHdrFtr.EVEN) {
            this.evenPageHeader = hdr;
        } else {
            this.defaultHeader = hdr;
        }

    }

    public XWPFHeader createHeader(Enum type) {
        return this.createHeader(type, (XWPFParagraph[]) null);
    }

    public XWPFHeader createHeader(Enum type, XWPFParagraph[] pars) {
        XWPFHeader header = this.getHeader(type);
//存在hdader则覆盖
//        if(header == null) {
        HdrDocument hdrDoc = Factory.newInstance();
        XWPFRelation relation = XWPFRelation.HEADER;
        int i = this.getRelationIndex(relation);
        XWPFHeader wrapper = (XWPFHeader) this.doc.createRelationship(relation, XWPFFactory.getInstance(), i);
        wrapper.setXWPFDocument(this.doc);
        CTHdrFtr hdr = this.buildHdr(type, wrapper, pars);
        wrapper.setHeaderFooter(hdr);
        hdrDoc.setHdr(hdr);
        this.assignHeader(wrapper, type);
        header = wrapper;
//        }

        return header;
    }

    public XWPFFooter createFooter(Enum type) {
        return this.createFooter(type, (XWPFParagraph[]) null);
    }

    public XWPFFooter createFooter(Enum type, XWPFParagraph[] pars) {
        XWPFFooter footer = this.getFooter(type);
//        if (footer == null) {
        FtrDocument ftrDoc = org.openxmlformats.schemas.wordprocessingml.x2006.main.FtrDocument.Factory.newInstance();
        XWPFRelation relation = XWPFRelation.FOOTER;
        int i = this.getRelationIndex(relation);
        XWPFFooter wrapper = (XWPFFooter) this.doc.createRelationship(relation, XWPFFactory.getInstance(), i);
        wrapper.setXWPFDocument(this.doc);
        CTHdrFtr ftr = this.buildFtr(type, wrapper, pars);
        wrapper.setHeaderFooter(ftr);
        ftrDoc.setFtr(ftr);
        this.assignFooter(wrapper, type);
        footer = wrapper;
//        }

        return footer;
    }

    private int getRelationIndex(XWPFRelation relation) {
        int i = 1;
        Iterator var3 = this.doc.getRelationParts().iterator();

        while (var3.hasNext()) {
            RelationPart rp = (RelationPart) var3.next();
            if (rp.getRelationship().getRelationshipType().equals(relation.getRelation())) {
                ++i;
            }
        }

        return i;
    }

    private CTHdrFtr buildFtr(Enum type, XWPFHeaderFooter wrapper, XWPFParagraph[] pars) {
        CTHdrFtr ftr = this.buildHdrFtr(pars, wrapper);
        this.setFooterReference(type, wrapper);
        return ftr;
    }

    private CTHdrFtr buildHdr(Enum type, XWPFHeaderFooter wrapper, XWPFParagraph[] pars) {
        CTHdrFtr hdr = this.buildHdrFtr(pars, wrapper);
        this.setHeaderReference(type, wrapper);
        return hdr;
    }

    private CTHdrFtr buildHdrFtr(XWPFParagraph[] paragraphs, XWPFHeaderFooter wrapper) {
        CTHdrFtr ftr = wrapper._getHdrFtr();
        if (paragraphs != null) {
            for (int i = 0; i < paragraphs.length; ++i) {
                ftr.addNewP();
                ftr.setPArray(i, paragraphs[i].getCTP());
            }
        }

        return ftr;
    }

    private void setFooterReference(Enum type, XWPFHeaderFooter wrapper) {
        CTHdrFtrRef ref = this.doc.getDocument().getBody().getSectPr().addNewFooterReference();
        ref.setType(type);
        ref.setId(this.doc.getRelationId(wrapper));
    }

    private void setHeaderReference(Enum type, XWPFHeaderFooter wrapper) {
        CTHdrFtrRef ref = this.doc.getDocument().getBody().isSetSectPr() ? this.doc.getDocument().getBody().getSectPr().addNewHeaderReference() : this.doc.getDocument().getBody().addNewSectPr().addNewHeaderReference();
        ref.setType(type);
        ref.setId(this.doc.getRelationId(wrapper));
    }

    public XWPFHeader getFirstPageHeader() {
        return this.firstPageHeader;
    }

    public XWPFFooter getFirstPageFooter() {
        return this.firstPageFooter;
    }

    public XWPFHeader getOddPageHeader() {
        return this.defaultHeader;
    }

    public XWPFFooter getOddPageFooter() {
        return this.defaultFooter;
    }

    public XWPFHeader getEvenPageHeader() {
        return this.evenPageHeader;
    }

    public XWPFFooter getEvenPageFooter() {
        return this.evenPageFooter;
    }

    public XWPFHeader getDefaultHeader() {
        return this.defaultHeader;
    }

    public XWPFFooter getDefaultFooter() {
        return this.defaultFooter;
    }

    public XWPFHeader getHeader(int pageNumber) {
        return pageNumber == 1 && this.firstPageHeader != null ? this.firstPageHeader : (pageNumber % 2 == 0 && this.evenPageHeader != null ? this.evenPageHeader : this.defaultHeader);
    }

    public XWPFHeader getHeader(Enum type) {
        return type == STHdrFtr.EVEN ? this.evenPageHeader : (type == STHdrFtr.FIRST ? this.firstPageHeader : this.defaultHeader);
    }

    public XWPFFooter getFooter(int pageNumber) {
        return pageNumber == 1 && this.firstPageFooter != null ? this.firstPageFooter : (pageNumber % 2 == 0 && this.evenPageFooter != null ? this.evenPageFooter : this.defaultFooter);
    }

    public XWPFFooter getFooter(Enum type) {
        return type == STHdrFtr.EVEN ? this.evenPageFooter : (type == STHdrFtr.FIRST ? this.firstPageFooter : this.defaultFooter);
    }

    public void createWatermark(String text) {
        XWPFParagraph[] pars = new XWPFParagraph[]{this.getWatermarkParagraph(text, 1)};
        this.createHeader(DEFAULT, pars);
        pars[0] = this.getWatermarkParagraph(text, 2);
        this.createHeader(FIRST, pars);
        pars[0] = this.getWatermarkParagraph(text, 3);
        this.createHeader(EVEN, pars);
    }

    /**
     * 原方法备份
     * @param text
     * @param idx
     * @return
     */
    private XWPFParagraph getWatermarkParagraph123(String text, int idx) {
        CTP p = org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP.Factory.newInstance();
        byte[] rsidr = this.doc.getDocument().getBody().getPArray(0).getRsidR();
        byte[] rsidrdefault = this.doc.getDocument().getBody().getPArray(0).getRsidRDefault();
        p.setRsidP(rsidr);
        p.setRsidRDefault(rsidrdefault);
        CTPPr pPr = p.addNewPPr();
        pPr.addNewPStyle().setVal("Header");
        CTR r = p.addNewR();
        CTRPr rPr = r.addNewRPr();
        rPr.addNewNoProof();
        CTPicture pict = r.addNewPict();
        CTGroup group = com.microsoft.schemas.vml.CTGroup.Factory.newInstance();
        CTShapetype shapetype = group.addNewShapetype();
        shapetype.setId("_x0000_t136");
        shapetype.setCoordsize("1600,21600");
        shapetype.setSpt(136.0F);
        shapetype.setAdj("10800");
        shapetype.setPath2("m@7,0l@8,0m@5,21600l@6,21600e");
        CTFormulas formulas = shapetype.addNewFormulas();
        formulas.addNewF().setEqn("sum #0 0 10800");
        formulas.addNewF().setEqn("prod #0 2 1");
        formulas.addNewF().setEqn("sum 21600 0 @1");
        formulas.addNewF().setEqn("sum 0 0 @2");
        formulas.addNewF().setEqn("sum 21600 0 @3");
        formulas.addNewF().setEqn("if @0 @3 0");
        formulas.addNewF().setEqn("if @0 21600 @1");
        formulas.addNewF().setEqn("if @0 0 @2");
        formulas.addNewF().setEqn("if @0 @4 21600");
        formulas.addNewF().setEqn("mid @5 @6");
        formulas.addNewF().setEqn("mid @8 @5");
        formulas.addNewF().setEqn("mid @7 @8");
        formulas.addNewF().setEqn("mid @6 @7");
        formulas.addNewF().setEqn("sum @6 0 @5");
        CTPath path = shapetype.addNewPath();
        path.setTextpathok(STTrueFalse.T);
        path.setConnecttype(STConnectType.CUSTOM);
        path.setConnectlocs("@9,0;@10,10800;@11,21600;@12,10800");
        path.setConnectangles("270,180,90,0");
        CTTextPath shapeTypeTextPath = shapetype.addNewTextpath();
        shapeTypeTextPath.setOn(STTrueFalse.T);
        shapeTypeTextPath.setFitshape(STTrueFalse.T);
        CTHandles handles = shapetype.addNewHandles();
        CTH h = handles.addNewH();
        h.setPosition("#0,bottomRight");
        h.setXrange("6629,14971");
        CTLock lock = shapetype.addNewLock();
        lock.setExt(STExt.EDIT);
        CTShape shape = group.addNewShape();
        shape.setId("PowerPlusWaterMarkObject" + idx);
        shape.setSpid("_x0000_s102" + (4 + idx));
        shape.setType("#_x0000_t136");
        shape.setStyle("position:absolute;margin-left:0;margin-top:0;width:415pt;height:207.5pt;z-index:1;mso-wrap-edited:f;mso-position-horizontal:center;mso-position-horizontal-relative:margin;mso-position-vertical:center;mso-position-vertical-relative:margin");
        shape.setWrapcoords("616 5068 390 16297 39 16921 -39 17155 7265 17545 7186 17467 -39 17467 18904 17467 10507 17467 8710 17545 18904 17077 18787 16843 18358 16297 18279 12554 19178 12476 20701 11774 20779 11228 21131 10059 21248 8811 21248 7563 20975 6316 20935 5380 19490 5146 14022 5068 2616 5068");
        shape.setFillcolor("black");
        shape.setStroked(STTrueFalse.FALSE);
        CTTextPath shapeTextPath = shape.addNewTextpath();
        shapeTextPath.setStyle("font-family:&quot;Cambria&quot;;font-size:1pt");
        shapeTextPath.setString(text);
        pict.set(group);
        return new XWPFParagraph(p, this.doc);
    }

    /**
     * 重写方法
     * @param text
     * @param idx
     * @return
     */
    private XWPFParagraph getWatermarkParagraph(String text, int idx) {
        CTP p = org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP.Factory.newInstance();
        byte[] rsidr = this.doc.getDocument().getBody().getPArray(0).getRsidR();
        byte[] rsidrdefault = this.doc.getDocument().getBody().getPArray(0).getRsidRDefault();
        p.setRsidP(rsidr);
        p.setRsidRDefault(rsidrdefault);
        CTPPr pPr = p.addNewPPr();
        pPr.addNewPStyle().setVal("Header");
        CTR r = p.addNewR();
        if (idx == 1) {
            //margin-left和margin-top调整位置，height和width调整大小
            //左上角
            setCTR(r, text, idx, "position:absolute;left:0pt;margin-left:-120pt;margin-top:108pt;mso-position-horizontal-relative:margin;mso-position-vertical-relative:margin;rotation:-2949120f;height:60pt;width:360pt;mso-width-relative:page;mso-height-relative:page");
            //右上角
            setCTR(r, text, idx, "position:absolute;left:0pt;margin-left:190pt;margin-top:108pt;mso-position-horizontal-relative:margin;mso-position-vertical-relative:margin;rotation:-2949120f;height:60pt;width:360pt;mso-width-relative:page;mso-height-relative:page");
            //左下角
            setCTR(r, text, idx, "position:absolute;left:0pt;margin-left:-120pt;margin-top:495pt;mso-position-horizontal-relative:margin;mso-position-vertical-relative:margin;rotation:-2949120f;height:60pt;width:360pt;mso-width-relative:page;mso-height-relative:page");
            //右下角
            setCTR(r, text, idx, "position:absolute;left:0pt;margin-left:190pt;margin-top:495pt;mso-position-horizontal-relative:margin;mso-position-vertical-relative:margin;rotation:-2949120f;height:60pt;width:360pt;mso-width-relative:page;mso-height-relative:page");
        } else {
            setCTR(r, text, idx, "position:absolute;left:0pt;mso-position-horizontal-relative:margin;mso-position-vertical-relative:margin;rotation:-2949120f;height:60pt;width:360pt;mso-width-relative:page;mso-height-relative:page");
        }
        return new XWPFParagraph(p, this.doc);
    }

    /**
     * 添加水印
     * @param r
     * @param text
     * @param idx
     * @param style
     */
    private void setCTR(CTR r, String text, int idx, String style) {
        CTRPr rPr = r.addNewRPr();
        rPr.addNewNoProof();
        CTPicture pict = r.addNewPict();
        CTGroup group = com.microsoft.schemas.vml.CTGroup.Factory.newInstance();
        CTShapetype shapetype = group.addNewShapetype();
        shapetype.setId("_x0000_t136");
        shapetype.setCoordsize("1600,21600");
        shapetype.setSpt(136.0F);
        shapetype.setAdj("10800");
        shapetype.setPath2("m@7,0l@8,0m@5,21600l@6,21600e");
        CTFormulas formulas = shapetype.addNewFormulas();
        formulas.addNewF().setEqn("sum #0 0 10800");
        formulas.addNewF().setEqn("prod #0 2 1");
        formulas.addNewF().setEqn("sum 21600 0 @1");
        formulas.addNewF().setEqn("sum 0 0 @2");
        formulas.addNewF().setEqn("sum 21600 0 @3");
        formulas.addNewF().setEqn("if @0 @3 0");
        formulas.addNewF().setEqn("if @0 21600 @1");
        formulas.addNewF().setEqn("if @0 0 @2");
        formulas.addNewF().setEqn("if @0 @4 21600");
        formulas.addNewF().setEqn("mid @5 @6");
        formulas.addNewF().setEqn("mid @8 @5");
        formulas.addNewF().setEqn("mid @7 @8");
        formulas.addNewF().setEqn("mid @6 @7");
        formulas.addNewF().setEqn("sum @6 0 @5");
        CTPath path = shapetype.addNewPath();
        path.setTextpathok(STTrueFalse.T);
        path.setConnecttype(STConnectType.CUSTOM);
        path.setConnectlocs("@9,0;@10,10800;@11,21600;@12,10800");
        path.setConnectangles("270,180,90,0");
        CTTextPath shapeTypeTextPath = shapetype.addNewTextpath();
        shapeTypeTextPath.setOn(STTrueFalse.T);
        shapeTypeTextPath.setFitshape(STTrueFalse.T);
        CTHandles handles = shapetype.addNewHandles();
        CTH h = handles.addNewH();
        h.setPosition("#0,bottomRight");
        h.setXrange("6629,14971");
        CTLock lock = shapetype.addNewLock();
        lock.setExt(STExt.EDIT);
        CTShape shape = group.addNewShape();
        shape.setId("PowerPlusWaterMarkObject" + idx);
        shape.setSpid("_x0000_s102" + (4 + idx));
        shape.setType("#_x0000_t136");
        shape.setStyle(style);
        shape.setWrapcoords("616 5068 390 16297 39 16921 -39 17155 7265 17545 7186 17467 -39 17467 18904 17467 10507 17467 8710 17545 18904 17077 18787 16843 18358 16297 18279 12554 19178 12476 20701 11774 20779 11228 21131 10059 21248 8811 21248 7563 20975 6316 20935 5380 19490 5146 14022 5068 2616 5068");
        //添加水印颜色
        shape.setFillcolor("#D3D3D3");
        shape.setStroked(STTrueFalse.FALSE);
        CTTextPath shapeTextPath = shape.addNewTextpath();
        shapeTextPath.setStyle("font-family:&quot;Cambria&quot;;font-size:1pt");
        shapeTextPath.setString(text);
        pict.set(group);
    }

    static {
        DEFAULT = STHdrFtr.DEFAULT;
        EVEN = STHdrFtr.EVEN;
        FIRST = STHdrFtr.FIRST;
    }
}
